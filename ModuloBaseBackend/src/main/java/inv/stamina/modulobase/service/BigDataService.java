/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import inv.stamina.modulobase.ApplicationMongoConfig;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author alepaco.com
 */
@Log4j2
@Service
@Component
public class BigDataService {

    @Autowired
    ApplicationMongoConfig mongoConfig;

    @Async
    public void deviceInfo(
            String ipClient,
            String form,
            String collectionName,
            Map<String, Object> request) {
        try {
            MongoDatabase db = mongoConfig.mongoDbFactory().getDb();
            MongoCollection<Document> collection = db.getCollection(collectionName);

            log.debug("collection " + collection);

            if (collection == null) {
                db.createCollection(collectionName);
                collection = db.getCollection(collectionName);
            }

            log.debug("Count " + collection.countDocuments());

            collection.insertOne(new Document(request));

            log.debug("Insert exitoso " + collectionName);
        } catch (Exception e) {
            log.error("Error " + e.getMessage(), e);
        }
    }
}
