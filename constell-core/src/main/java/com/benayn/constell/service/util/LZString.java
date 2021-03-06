package com.benayn.constell.service.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Port javascript lz-string in JAVA version. https://github.com/rufushuang/lz-string4java
 * Port from original JavaScript version. https://github.com/pieroxy/lz-string
 */
public class LZString {

    private LZString() { }

    private static char[] keyStrBase64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static char[] keyStrUriSafe = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-$".toCharArray();
    private static Map<char[], Map<Character, Integer>> baseReverseDic = Maps.newHashMap();

    private static char getBaseValue(char[] alphabet, Character character) {
        Map<Character, Integer> map = baseReverseDic.get(alphabet);
        if (map == null) {
            map = Maps.newHashMap();
            baseReverseDic.put(alphabet, map);
            for (int i = 0; i < alphabet.length; i++) {
                map.put(alphabet[i], i);
            }
        }

        return (char) map.get(character).intValue();
    }

    public static String encodes(Object object) {
        return CharMatcher.whitespace().trimFrom(compressToEncodedURIComponent(JSON.toJSONString(object)));
    }

    public static Map<String, Object> decodesAsMap(String target) {
        return decodes(target, new TypeReference<HashMap<String, Object>>(){});
    }

    public static <T> T decodes(String target, TypeReference<T> typeReference) {
        if (Strings.isNullOrEmpty(target)) {
            return null;
        }

        return JSON.parseObject(decodes(target), typeReference);
    }

    public static <T> T decodes(String target, Class<T> clazz) {
        if (Strings.isNullOrEmpty(target)) {
            return null;
        }

        return JSON.parseObject(decodes(target), clazz);
    }

    public static String decodes(String target) {
        return decompressFromEncodedURIComponent(target);
    }

    public static String compressToBase64(String input) {
        if (input == null)
            return "";
        String res = LZString._compress(input, 6, new CompressFunctionWrapper() {
            @Override
            public Character doFunc(int a) {
                return keyStrBase64[a];
            }
        });
        switch (res.length() % 4) { // To produce valid Base64
            default: // When could this happen ?
            case 0:
                return res;
            case 1:
                return res + "===";
            case 2:
                return res + "==";
            case 3:
                return res + "=";
        }
    }

    public static String decompressFromBase64(String inputStr) {
        if (inputStr == null) return "";
        if (Objects.equals(inputStr, "")) return null;
        final char[] input = inputStr.toCharArray();

        return LZString._decompress(input.length, 32, new DecompressFunctionWrapper() {
            @Override
            public Character doFunc(int index) {
                return getBaseValue(keyStrBase64, input[index]);
            }
        });
    }

    public static String compressToUTF16(String input) {
        if (input == null)
            return "";
        return LZString._compress(input, 15, new CompressFunctionWrapper() {
            @Override
            public Character doFunc(int a) {
                return fc(a + 32);
            }
        }) + " ";
    }

    public static String decompressFromUTF16(String compressedStr) {
        if (compressedStr == null) return "";
        if (Objects.equals(compressedStr, "")) return null;

        final char[] compressed = compressedStr.toCharArray();
        return LZString._decompress(compressed.length, 16384, new DecompressFunctionWrapper() {
            @Override
            public Character doFunc(int index) {
                return (char) (compressed[index] - 32);
            }
        });
    }

    public static String compressToEncodedURIComponent(String input) {
        if (input == null)
            return "";
        return LZString._compress(input, 6, new CompressFunctionWrapper() {
            @Override
            public Character doFunc(int a) {
                return keyStrUriSafe[a];
            }
        }) + " ";
    }

    public static String decompressFromEncodedURIComponent(String inputStr) {
        if (inputStr == null) return "";
        if (Objects.equals(inputStr, "")) return null;
        inputStr = inputStr.replace(' ', '+');
        final char[] input = inputStr.toCharArray();
        return LZString._decompress(input.length, 32, new DecompressFunctionWrapper() {
            @Override
            public Character doFunc(int index) {
                return getBaseValue(keyStrUriSafe, input[index]);
            }
        });
    }

    private static abstract class CompressFunctionWrapper {
        public abstract Character doFunc(int i);
    }

    public static String compress(String uncompressed) {
        return LZString._compress(uncompressed, 16, new CompressFunctionWrapper() {
            @Override
            public Character doFunc(int a) {
                return fc(a);
            }
        });
    }
    private static String _compress(String uncompressedStr, int bitsPerChar, CompressFunctionWrapper getCharFromInt) {
        if (uncompressedStr == null) return "";
        int i, value;
        Map<String, Integer> context_dictionary = Maps.newHashMap();
        Set<String> context_dictionaryToCreate = Sets.newHashSet();
        String context_c;
        String context_wc;
        String context_w = "";
        double context_enlargeIn = 2d; // Compensate for the first entry which should not count
        int context_dictSize = 3;
        int context_numBits = 2;
        List<Character> context_data = Lists.newArrayListWithCapacity(uncompressedStr.length() / 3);
        int context_data_val = 0;
        int context_data_position = 0;
        int ii;

        char[] uncompressed = uncompressedStr.toCharArray();
        for (ii = 0; ii < uncompressed.length; ii += 1) {
            context_c = String.valueOf(uncompressed[ii]);
            if (!context_dictionary.containsKey(context_c)) {
                context_dictionary.put(context_c, context_dictSize++);
                context_dictionaryToCreate.add(context_c);
            }

            context_wc = context_w + context_c;
            if (context_dictionary.containsKey(context_wc)) {
                context_w = context_wc;
            } else {
                if (context_dictionaryToCreate.contains(context_w)) {
                    if (context_w.charAt(0) < 256) {
                        for (i = 0; i < context_numBits; i++) {
                            context_data_val = (context_data_val << 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.add(getCharFromInt.doFunc(context_data_val));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                        }
                        value = context_w.charAt(0);
                        for (i = 0; i < 8; i++) {
                            context_data_val = (context_data_val << 1) | (value & 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.add(getCharFromInt.doFunc(context_data_val));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value = value >> 1;
                        }
                    } else {
                        value = 1;
                        for (i = 0; i < context_numBits; i++) {
                            context_data_val = (context_data_val << 1) | value;
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.add(getCharFromInt.doFunc(context_data_val));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value = 0;
                        }
                        value = context_w.charAt(0);
                        for (i = 0; i < 16; i++) {
                            context_data_val = (context_data_val << 1) | (value & 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.add(getCharFromInt.doFunc(context_data_val));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value = value >> 1;
                        }
                    }
                    context_enlargeIn--;
                    if (context_enlargeIn == 0) {
                        context_enlargeIn = Math.pow(2, context_numBits);
                        context_numBits++;
                    }
                    context_dictionaryToCreate.remove(context_w);
                } else {
                    value = context_dictionary.get(context_w);
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.add(getCharFromInt.doFunc(context_data_val));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value = value >> 1;
                    }

                }
                context_enlargeIn--;
                if (context_enlargeIn == 0) {
                    context_enlargeIn = Math.pow(2, context_numBits);
                    context_numBits++;
                }
                // Add wc to the dictionary.
                context_dictionary.put(context_wc, context_dictSize++);
                context_w = context_c;
            }
        }

        // Output the code for w.
        if (!context_w.isEmpty()) {
            if (context_dictionaryToCreate.contains(context_w)) {
                if (context_w.charAt(0) < 256) {
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.add(getCharFromInt.doFunc(context_data_val));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                    }
                    value = context_w.charAt(0);
                    for (i = 0; i < 8; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.add(getCharFromInt.doFunc(context_data_val));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value = value >> 1;
                    }
                } else {
                    value = 1;
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1) | value;
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.add(getCharFromInt.doFunc(context_data_val));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value = 0;
                    }
                    value = context_w.charAt(0);
                    for (i = 0; i < 16; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.add(getCharFromInt.doFunc(context_data_val));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value = value >> 1;
                    }
                }
                context_enlargeIn--;
                if (context_enlargeIn == 0) {
                    context_enlargeIn = Math.pow(2, context_numBits);
                    context_numBits++;
                }
                context_dictionaryToCreate.remove(context_w);
            } else {
                value = context_dictionary.get(context_w);
                for (i = 0; i < context_numBits; i++) {
                    context_data_val = (context_data_val << 1) | (value & 1);
                    if (context_data_position == bitsPerChar - 1) {
                        context_data_position = 0;
                        context_data.add(getCharFromInt.doFunc(context_data_val));
                        context_data_val = 0;
                    } else {
                        context_data_position++;
                    }
                    value = value >> 1;
                }

            }

            context_enlargeIn--;
            if (context_enlargeIn == 0) {
                //noinspection UnusedAssignment
                context_enlargeIn = Math.pow(2, context_numBits);
                context_numBits++;
            }

        }

        // Mark the end of the stream
        value = 2;
        for (i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1) | (value & 1);
            if (context_data_position == bitsPerChar - 1) {
                context_data_position = 0;
                context_data.add(getCharFromInt.doFunc(context_data_val));
                context_data_val = 0;
            } else {
                context_data_position++;
            }
            value = value >> 1;
        }

        // Flush the last char
        while (true) {
            context_data_val = (context_data_val << 1);
            if (context_data_position == bitsPerChar - 1) {
                context_data.add(getCharFromInt.doFunc(context_data_val));
                break;
            }
            else
                context_data_position++;
        }
        StringBuilder sb = new StringBuilder(context_data.size());
        for (char c : context_data) sb.append(c);
        return sb.toString();
    }

    private static abstract class DecompressFunctionWrapper {
        public abstract Character doFunc(int i);
    }

    protected static class DecData {
        public char val;
        public int position;
        public int index;
    }

    public static String f(int i) {
        return String.valueOf((char) i);
    }
    public static Character fc(int i) {
        return (char) i;
    }

    public static String decompress(final String compressed) {
        if (compressed == null) return "";
        if (Objects.equals(compressed, "")) return null;

        return LZString._decompress(compressed.length(), 32768, new DecompressFunctionWrapper() {
            char[] compChars = compressed.toCharArray();

            @Override
            public Character doFunc(int i) {
                return compChars[i];
            }
        });
    }
    private static String _decompress(int length, int resetValue, DecompressFunctionWrapper getNextValue) {
        List<String> dictionary = Lists.newArrayList();
        @SuppressWarnings("unused")
        int next;
        double enlargeIn = 4d;
        int dictSize = 4;
        int numBits = 3;
        String entry;
        List<String> result = Lists.newArrayList();
        String w;
        int bits, resb; int maxpower, power;
        String c = null;
        DecData data = new DecData();
        data.val = getNextValue.doFunc(0);
        data.position = resetValue;
        data.index = 1;

        for (int i = 0; i < 3; i += 1) {
            dictionary.add(i, f(i));
        }

        bits = 0;
        maxpower = (int) Math.pow(2, 2);
        power = 1;
        while (power != maxpower) {
            resb = data.val & data.position;
            data.position >>= 1;
            if (data.position == 0) {
                data.position = resetValue;
                data.val = getNextValue.doFunc(data.index++);
            }
            bits |= (resb > 0 ? 1 : 0) * power;
            power <<= 1;
        }

        //noinspection UnusedAssignment
        switch (next = bits) {
            case 0:
                bits = 0;
                maxpower = (int) Math.pow(2,8);
                power=1;
                while (power != maxpower) {
                    resb = data.val & data.position;
                    data.position >>= 1;
                    if (data.position == 0) {
                        data.position = resetValue;
                        data.val = getNextValue.doFunc(data.index++);
                    }
                    bits |= (resb>0 ? 1 : 0) * power;
                    power <<= 1;
                }
                c = f(bits);
                break;
            case 1:
                bits = 0;
                maxpower = (int) Math.pow(2,16);
                power=1;
                while (power!=maxpower) {
                    resb = data.val & data.position;
                    data.position >>= 1;
                    if (data.position == 0) {
                        data.position = resetValue;
                        data.val = getNextValue.doFunc(data.index++);
                    }
                    bits |= (resb>0 ? 1 : 0) * power;
                    power <<= 1;
                }
                c = f(bits);
                break;
            case 2:
                return "";
        }
        dictionary.add(3, c);
        w = c;
        result.add(w);
        while (true) {
            if (data.index > length) {
                return "";
            }

            bits = 0;
            maxpower = (int) Math.pow(2,numBits);
            power=1;
            while (power!=maxpower) {
                resb = data.val & data.position;
                data.position >>= 1;
                if (data.position == 0) {
                    data.position = resetValue;
                    data.val = getNextValue.doFunc(data.index++);
                }
                bits |= (resb>0 ? 1 : 0) * power;
                power <<= 1;
            }

            int cc;
            switch (cc = bits) {
                case 0:
                    bits = 0;
                    maxpower = (int) Math.pow(2,8);
                    power=1;
                    while (power!=maxpower) {
                        resb = data.val & data.position;
                        data.position >>= 1;
                        if (data.position == 0) {
                            data.position = resetValue;
                            data.val = getNextValue.doFunc(data.index++);
                        }
                        bits |= (resb>0 ? 1 : 0) * power;
                        power <<= 1;
                    }

                    dictionary.add(dictSize++, f(bits));
                    cc = dictSize-1;
                    enlargeIn--;
                    break;
                case 1:
                    bits = 0;
                    maxpower = (int) Math.pow(2,16);
                    power=1;
                    while (power!=maxpower) {
                        resb = data.val & data.position;
                        data.position >>= 1;
                        if (data.position == 0) {
                            data.position = resetValue;
                            data.val = getNextValue.doFunc(data.index++);
                        }
                        bits |= (resb>0 ? 1 : 0) * power;
                        power <<= 1;
                    }
                    dictionary.add(dictSize++, f(bits));
                    cc = dictSize-1;
                    enlargeIn--;
                    break;
                case 2:
                    StringBuilder sb = new StringBuilder(result.size());
                    for (String s : result)sb.append(s);
                    return sb.toString();
            }

            if (enlargeIn == 0) {
                enlargeIn = Math.pow(2, numBits);
                numBits++;
            }

            if (cc < dictionary.size() && dictionary.get(cc) != null) {
                entry = dictionary.get(cc);
            } else {
                if (cc == dictSize) {
                    assert w != null;
                    entry = w + w.charAt(0);
                } else {
                    return null;
                }
            }
            result.add(entry);

            // Add w+entry[0] to the dictionary.
            dictionary.add(dictSize++, w + entry.charAt(0));
            enlargeIn--;

            w = entry;

            if (enlargeIn == 0) {
                enlargeIn = Math.pow(2, numBits);
                numBits++;
            }

        }

    }

}