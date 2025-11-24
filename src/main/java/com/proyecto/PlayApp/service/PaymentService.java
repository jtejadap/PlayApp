package com.proyecto.PlayApp.service;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

@Service
public class PaymentService {

    public String crearPreferencia(String title, BigDecimal amount, int quantity, String ordenId)
        throws MPException, MPApiException {

        MercadoPagoConfig.setAccessToken("APP_USR-3404440688881887-112017-2822c3f4ff28fc67d00836b270dc7fa6-3005390419");

        PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                        .title(title)
                        .quantity(quantity)
                        .unitPrice(new BigDecimal(amount.intValue()))
                        .currencyId("COP")
                        .build();

        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("https://abram-wonted-sharita.ngrok-free.dev/payment/proceed?estado=SUCCESS&orden=" + ordenId)
                        .pending("https://abram-wonted-sharita.ngrok-free.dev/payment/proceed?estado=PENDING&orden=" + ordenId)
                        .failure("https://abram-wonted-sharita.ngrok-free.dev/payment/proceed?estado=FAILED&orden=" + ordenId)
                        .build();

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(Collections.singletonList(item))
                        .backUrls(backUrls)
                        .autoReturn("approved")
                        .build();

        PreferenceClient client = new PreferenceClient();
        try {
            Preference preference = client.create(preferenceRequest);
            return preference.getInitPoint();
        } catch (MPApiException apiEx) {
            throw apiEx;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }
}
