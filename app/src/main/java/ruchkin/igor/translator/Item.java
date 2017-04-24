package ruchkin.igor.translator;


public class Item {

    String word,trans,lang_code;
    boolean box;


    Item(String _word, String _trans,  String _code, boolean _box) {
        word = _word;
        trans = _trans;
        lang_code = _code;
        box = _box;
    }
}