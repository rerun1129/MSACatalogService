package com.example.catalogservice.messagequeue;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CatalogRepository catalogRepository;

    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage) {
        log.info ( "Kafka Message : ->" + kafkaMessage );
        HashMap <Object, Object> map = new HashMap <> ( );
        ObjectMapper mapper = new ObjectMapper ( );
        try {
            map = mapper.readValue ( kafkaMessage, new TypeReference <> ( ) { } );
        }catch ( JsonProcessingException ex ){
            ex.printStackTrace ();
        }
        CatalogEntity entity = catalogRepository.findByProductId ( ( String ) map.get ( "productId" ) );
        if(entity != null) {
            entity.setStock ( entity.getStock ( ) - ( Integer ) map.get ( "qty" ) );
            catalogRepository.save ( entity );
        }
    }
}
