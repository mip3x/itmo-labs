package sentenceCore;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

import interfaces.SentenceMember;
import services.Order;
import services.MemberType;

public abstract class CoreSpeech implements SentenceMember {
    protected Map<String, Object> content;

    private final String mainWordKey = "mainWord";
    private final String characteristicKey = "characteristic";
    private final String orderKey = "order";

    public CoreSpeech(String mainWord, String characteristic, Order order) {
        content = new HashMap<String, Object>();

        content.put(mainWordKey, mainWord);
        characteristic = checkCharacteristic(characteristic);
        content.put(characteristicKey, characteristic);
        content.put(orderKey, order);
    }

    public CoreSpeech(String mainWord, String characteristic) {
        content = new HashMap<String, Object>();

        content.put(mainWordKey, mainWord);
        characteristic = checkCharacteristic(characteristic);
        content.put(characteristicKey, characteristic);
    }

    public CoreSpeech(String mainWord, MemberType memberType) {
        content = new HashMap<String, Object>();
        String key = (memberType == MemberType.OBJECT) ? "mainWord" : "characteristic";

        content.put(key, mainWord);
    }

    protected Object getMainWord() {
        return content.get(mainWordKey);
    }

    protected void setMainWord(String mainWord) {
        content.put(mainWordKey, mainWord);
    }

    public Object getCharacteristic() {
        return content.get(characteristicKey);
    }

    protected void setCharacteristic(String characteristic) {
        characteristic = checkCharacteristic(characteristic);
        content.put(characteristicKey, characteristic);
    }

    private String checkCharacteristic(String characteristic) {
        if (characteristic.contains("вш") || characteristic.contains("ива")) return ", " + characteristic;
        else return characteristic;
    }

    protected Object getOrder() {
        return content.get(orderKey);
    }

    protected void setOrder(Order order) {
        content.put(orderKey, order);
    }

    @Override
    public String getSentenceMemberCharacters() {
        String result = StringUtils.EMPTY;

        if (content.containsKey(mainWordKey)) {
            if (content.containsKey(characteristicKey)) {
                if (content.containsKey(orderKey)) {
                    result = ((Order)content.get(orderKey) == Order.DIRECT)
                        ? content.get(characteristicKey) + " " + (String)content.get(mainWordKey)
                        : (String)content.get(mainWordKey) + " " + content.get(characteristicKey);
                }
                else {
                    result = content.get(characteristicKey) + " " + (String)content.get(mainWordKey);
                }
            }
            else result = (String)content.get(mainWordKey);
        }
        else result = (String)content.get(characteristicKey); 

        return result;
    }
}
