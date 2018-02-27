/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping(value = "/orders")
public class OrdersAPIController {
    @Autowired
    private final RestaurantOrderServicesStub restaurantStub;

    public OrdersAPIController(RestaurantOrderServicesStub restaurantStub){
        this.restaurantStub = restaurantStub;
    }
    
    @RequestMapping(method = RequestMethod.PUT, path = "/{idmesa}")
    public ResponseEntity<?> updateOrder(@PathVariable String idmesa, @RequestBody String nOrder){
        JSONObject obj =  new JSONObject(nOrder);
        Iterator<?> keys = obj.keys();
        while(keys.hasNext()){
            String key = (String) keys.next();
            try {
                restaurantStub.getTableOrder(Integer.parseInt(idmesa)).addDish(key, obj.getInt(key));               
            } catch (Exception ex) {
                Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
         }
         return new ResponseEntity<>(HttpStatus.ACCEPTED);               
         
     }
    @RequestMapping(method = RequestMethod.DELETE, path = "/{idmesa}")
    public ResponseEntity<?> deleteOrder(@PathVariable String idmesa){
        try {
            restaurantStub.releaseTable(Integer.parseInt(idmesa));
            return new ResponseEntity<>(HttpStatus.OK);
         } catch (Exception ex) {
             Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
             return new ResponseEntity<>(HttpStatus.CONFLICT);
         }
     }
    @GetMapping("/{idmesa}")
    public ResponseEntity getOrder(@PathVariable int idmesa) {
        try {
            Map<String, Integer> amounts =  new ConcurrentHashMap();  
            amounts = restaurantStub.getTableOrder(idmesa).getOrderAmountsMap();
            String json = new ObjectMapper().writeValueAsString(amounts);
            return  new ResponseEntity<>(json,HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{idmesa}/total")
    public ResponseEntity getOrderCost(@PathVariable int idmesa) {
        try {
            Map<Integer,Order> mapOrders = new ConcurrentHashMap<>();
            int orderCost = restaurantStub.calculateTableBill(idmesa);
            return new ResponseEntity<>(orderCost,HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
    @RequestMapping(method = RequestMethod.POST)	
	public ResponseEntity<?> manejadorPostRecursoXX(@RequestBody String orden){
            try {
                JSONObject obj =  new JSONObject(orden);
                Iterator<?> keys = obj.keys();
                Order ord = new Order(obj.getInt("TABLE"));
                while(keys.hasNext() ){
                    String key = (String)keys.next();
                    if(key!="TABLE"){                        
                        JSONObject nOrder = obj.getJSONObject("ORDER");
                        Iterator<?> nKeys = nOrder.keys();
                        while(nKeys.hasNext()){
                            String nKey = (String) nKeys.next();
                            ord.addDish(nKey, nOrder.getInt(nKey));                                        
                        
                        }
                        
                    }
                }                    
                restaurantStub.addNewOrderToTable(ord);
		return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception ex) {
		Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.METHOD_NOT_ALLOWED);            
            }        
	}
    
    
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetRecursoXX(){
        try {
            Set<Integer> keys =  restaurantStub.getTablesWithOrders();
            Map<String, Integer> amounts =  new ConcurrentHashMap();                          
            for(Integer in: keys){
                Order o = restaurantStub.getTableOrder(in);
                amounts.putAll(o.getOrderAmountsMap());                
            }
            String json = new ObjectMapper().writeValueAsString(amounts);
            return  new ResponseEntity<>(json,HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
        }  
    }      
}
    

