package wellness.shop.Services;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stripe.Stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wellness.shop.Models.BillingOrder.CartItem;
import wellness.shop.Models.BillingOrder.PaymentStatus;
import wellness.shop.Repositories.StripeRepository;
import wellness.shop.Security.JWT;
import wellness.shop.Utilities.UtilitiesGeneral;


import java.math.BigDecimal;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    @Value("${jar.java.url}")
    private String backEndURL;
    @Autowired
    private StripeRepository stripeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private JWT jwt;
    private ObjectMapper objectMapper;



//    @Autowired
//    private PromoService promoService;
//    @Autowired
//    private MailService mailService;


//    private final String REFUND_SIZE = "0.5";
//    private final int REFUND_DAYS_APPLIED = 7;

    public StripeService(){
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;

    }

    //////////////////////////////////////////// Payment /////////////////////////////////////////////////////////////
    public Session createCheckoutSession(Integer orderID, String authorizationHeader) {

        String userUUID = jwt.getUUID(authorizationHeader);
        String secretUUID = UtilitiesGeneral.generateUID().toString();

        int paymentID = stripeRepository.createPayment(userUUID,secretUUID,orderID);
        if(paymentID == 0) return null;

        Long orderPrice = calculateOrderPrice(orderID);
        String OrderCurrency = "EUR";

        String successUrl = backEndURL +"/noFilter/stripe/" + userUUID + "/" + secretUUID + "/" + paymentID;
        String cancelUrl = backEndURL +"/noFilter/stripe/" + userUUID + "/" + paymentID;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("paymentID", paymentID + "");
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(OrderCurrency)
                                .setUnitAmount(orderPrice)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Moketi:")
                                        .build())
                                .build())
                        .build())
                .putAllMetadata(metadata)
                .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            System.out.println("StripeException: " + e.getMessage());
            return null;
        }
        stripeRepository.setPaymentIntentID(paymentID, session.getPaymentIntent());
        return session;
    }

    private Long calculateOrderPrice(int orderID){

        List<CartItem> cartItemList = getOrderItemArrayByOrderID(orderID);

        if(cartItemList == null) return 0L;

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cartItemList) {
            BigDecimal price = productService.getPriceById(item.getProductId());
            if (price == null) continue;

            price = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            String category = item.getCategory();

            if (category.equals("Service")) {
                orderService.registerDietServiceInternal(orderID,item.getProductId());
            }
            totalPrice = totalPrice.add(price);
        }

        totalPrice = totalPrice.multiply(BigDecimal.valueOf(100));
        return totalPrice.longValue();

    }

    private List<CartItem> getOrderItemArrayByOrderID(int orderID){
    String jsonItemString = orderService.getProductJsonByOrderIDInternal(orderID);
    return generateObjectFromJSon(jsonItemString, new TypeReference<List<CartItem>>() {});
    }

    public void setProcessPaymentStatus(int paymentID, String userUUID, String secretUUID){

        int orderID = stripeRepository.getOrderID(paymentID,userUUID);

        if(orderID == 0) return;

        PaymentStatus paymentStatus = orderService.getPaymentStatusInternal(orderID);
        if(paymentStatus != PaymentStatus.PENDING) return;

        if (secretUUID != null && stripeRepository.checkIfPaymentValid(paymentID,userUUID,secretUUID)) {
            orderService.setPaymentStatusInternal(orderID,PaymentStatus.PAID);
            orderService.setPaymentDietServicePaymentStatus(orderID,PaymentStatus.PAID);
            String refundKey = UtilitiesGeneral.generateUID().toString();
            stripeRepository.setRefundKey(paymentID,refundKey);
            //<---------------- if needed here later will add promo code count
            //<---------------- if needed here will add send mail that payment was good and add refund key
        }
        else orderService.setPaymentStatusInternal(orderID, PaymentStatus.CANCELLED);

    }

    ////////////////////////////////////// REFUND ////////////////////////////////////////////////////////////////

        public String refund(String refundKey){

        String paymentIntentID = stripeRepository.getPaymentIntentIDByRefundKey(refundKey);
        int orderID = stripeRepository.getOrderIDByRefundKey(refundKey);

        if(orderService.getPaymentStatusInternal(orderID) != PaymentStatus.PAID) return "No refund available";

        if(paymentIntentID == null) return "RefundKey not Found";

        String chargeID = getPaymentChargeID(paymentIntentID);
        Long amountPayed =  getPaymentAmount(paymentIntentID);

        if (chargeID == null || amountPayed == null )  return "Invalid payment intent key";

        RefundCreateParams params =  RefundCreateParams.builder().setCharge(chargeID).setAmount(amountPayed).build();

        try {
            Refund refund = Refund.create(params);
            System.out.println("Refund status: " + refund.getStatus());
            System.out.println("Refund amount: " + refund.getAmount());

            orderService.setPaymentStatusInternal(orderID,PaymentStatus.REFUNDED);
        } catch (StripeException e) {
            return "No refund available";
        }

        return "Refund was successful";

    }

        private Long getPaymentAmount(String paymentIntentId){

        long amount;
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            amount = paymentIntent.getAmount();

        } catch (StripeException e) {
            System.out.println("StripeException: " + e.getMessage());
            return null;
        }
        return amount;
    }

        private String getPaymentChargeID(String paymentIntentId){

        String chargeID;
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            chargeID = paymentIntent.getCharges().getData().get(0).getId();

        } catch (StripeException e) {
            System.out.println("StripeException: " + e.getMessage());
            return null;
        }
        return chargeID;
    }
















    private String getUserPaymentEmail(int paymentID){
        String paymentIntentID = stripeRepository.getPaymentIntentID(paymentID);

        PaymentIntent paymentIntent = getPaymentIntentByPaymentIntentID(paymentIntentID);
        String customerID = paymentIntent.getCustomer();

        return getCustomerEmailByCustomerID(customerID);

    }

    private PaymentIntent getPaymentIntentByPaymentIntentID(String paymentIntentId){

        PaymentIntent paymentIntent = null;
        try {
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }
        return paymentIntent;

    }

    private String getCustomerEmailByCustomerID(String customerId){
        Customer customer;
        String customerEmail = "";
        try {
            customer = Customer.retrieve(customerId);
            customerEmail = customer.getEmail();
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }
        return customerEmail;
    }




//
//
//    public HashMap<String,String> refundByOrderID(int orderID, String authorizationHeader ){
//
//        HashMap<String,String> responseHashMap = new HashMap<>();
//
//        if(!userService.userAutoLogIn(authorizationHeader)) {
//            responseHashMap.put("response","No authorization");
//            return responseHashMap;
//        }
//
//        responseHashMap.putAll(refund(orderID));
//
//        return responseHashMap;
//    }
//
//    public Long checkRefundSize(String refundKey){
//
//        int orderID = stripeRepository.getOrderIDWithUUID(refundKey);;
//
//        return calculateRefundTotalPrice(orderID);
//    }
//
//    public HashMap<String,String> refundByRefundKey(String refundKey){
//
//        int orderID = stripeRepository.getOrderIDWithUUID(refundKey);
//
//        return refund(orderID);
//    }
//

//

//

//

//
//
//

//

//

//
//
//

//
//    private Long calculateRefundTotalPrice(int orderID){
//
//        HashMap<String, CartListItem> cartList = getOrderItemArrayByOrderID(orderID);
//        long totalPrice = 0;
//        LocalDate today = LocalDate.now();
//
//        for (String key : cartList.keySet()) {
//            int quantity = cartList.get(key).getQuantity();
//            Event event = eventService.getItemByID(cartList.get(key).getId());
//            BigDecimal price = event.getPrice();
//            LocalDate eventDate = event.getDate().toLocalDate();
//            price = price.multiply(new BigDecimal(100));
//            price = price.multiply(new BigDecimal(quantity));
//
//            if(today.isAfter(eventDate.minusDays(REFUND_DAYS_APPLIED))) price = price.multiply(new BigDecimal(REFUND_SIZE));
//            if(today.isAfter(eventDate)) price = price.multiply(BigDecimal.ZERO);
//
//            totalPrice += price.longValue();
//        }
//
//        String promoCode = orderService.getUsedPromo(orderID);
//        double promoSize = promoService.checkPromo(promoCode);
//
//        return (long) (totalPrice * promoSize);
//    }


    private <T> T generateObjectFromJSon(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}