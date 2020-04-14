package com.ditecting.honeyeye.pcap4j.extension.utils;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.namednumber.NamedNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 10:19
 */
@Slf4j
public class GsonUtils {
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private GsonUtils() {
    }

    public Gson getGson(){ return  gson;}


    public static List<String> getJsonPacket (String rawJson, NamedNumber number, boolean cascade, List<String> myChildren){
        List<String> jsonList = new ArrayList<>();

        JsonElement jsonElement =  JsonParser.parseString(rawJson);
        if(jsonElement.isJsonArray()){
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for(JsonElement element : jsonArray){
                if(element.isJsonObject()){
                    jsonList = pickJsonPacket(element.getAsJsonObject(), number, cascade, myChildren);
                    if(jsonList.size() > 0){
                        return jsonList;
                    }
                }
            }
        } else if(jsonElement.isJsonObject()){
            jsonList = pickJsonPacket(jsonElement.getAsJsonObject(), number, cascade, myChildren);
            if(jsonList.size() > 0){
                return jsonList;
            }
        }

        throw new JsonParseException("rawJson is not a valid json data: [" + rawJson + "].");
    }

    private static List<String> pickJsonPacket (JsonObject obj, NamedNumber number, boolean cascade, List<String> myChildren) {
        List<String> jsonList = new ArrayList<>();

        if(obj.has("_source")){
            JsonObject obj__source = obj.getAsJsonObject("_source");
            if(obj__source.has("layers")){
                JsonObject obj_layers = obj__source.getAsJsonObject("layers");
                if(obj_layers.has(number.name())){
                    jsonList.add(MatchProtocolEnum.MATCHED.name());
                    JsonObject obj_app = obj_layers.getAsJsonObject(number.name());
                    jsonList.add("\"" + number.name()+ "\":" + obj_app.toString());
                    if(cascade){
                        int i = 0;
                        if(myChildren != null && myChildren.size() > 0){
                            for (String sub : myChildren){
                                JsonObject obj_app_sub = obj_layers.getAsJsonObject(sub);
                                jsonList.add("\"" + sub + "\":" + obj_app_sub.toString());
                            }
                        }
                    }
                    return jsonList;
                }else {
//                    log.warn("another protocol is detected on the port[" + number.value() + ": " + number.name() + "]");
                    jsonList.add(MatchProtocolEnum.UNMATCHED.name());
                    return jsonList;
                }
            }
        }

        return jsonList;
    }

    public static JsonArray getJsonPacketArray (String rawJson){
        JsonElement jsonElement =  JsonParser.parseString(rawJson);
        if(jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }else
            return null;
    }
}