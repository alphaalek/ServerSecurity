package me.alek.utils.bytecodeidentifier;

import java.util.HashMap;

public class CharSerializer {

    public class Builder {

        private final HashMap<Integer, String> map;

        public Builder() {
            this.map = new HashMap<>();
        }

        public Builder put(int i, String str) {
            map.put(i, str);
            return this;
        }

        public HashMap<Integer, String> build() {
            return map;
        }

    }

    private final HashMap<Integer, String> serializerMap;

    public String serializeInt(int i) {
        if (serializerMap.containsKey(i)) {
            return serializerMap.get(i);
        }
        return "?";
    }

    public CharSerializer() {
        Builder builder = new Builder();
        serializerMap = builder
                .put(0, "A")
                .put(1, "B")
                .put(2, "C")
                .put(3, "D")
                .put(4, "E")
                .put(5, "F")
                .put(6, "G")
                .put(7, "H")
                .put(8, "I")
                .put(9, "J")
                .put(10, "K")
                .put(14, "L")
                .put(16, "M")
                .put(17, "N")
                .put(18, "O")
                .put(21, "P")
                .put(22, "Q")
                .put(23, "R")
                .put(24, "S")
                .put(20, "T")
                .put(25, "U")
                .put(46, "V")
                .put(47, "W")
                .put(48, "X")
                .put(49, "Y")
                .put(50, "Z")
                .put(51, "a")
                .put(52, "b")
                .put(53, "c")
                .put(54, "d")
                .put(55, "e")
                .put(56, "f")
                .put(57, "g")
                .put(58, "h")
                .put(79, "i")
                .put(80, "j")
                .put(81, "k")
                .put(82, "l")
                .put(83, "m")
                .put(84, "n")
                .put(85, "o")
                .put(86, "p")
                .put(87, "q")
                .put(89, "r")
                .put(90, "s")
                .put(92, "t")
                .put(96, "u")
                .put(97, "v")
                .put(100, "w")
                .put(101, "x")
                .put(104, "y")
                .put(105, "z")
                .put(112, "0")
                .put(116, "1")
                .put(120, "2")
                .put(122, "3")
                .put(124, "4")
                .put(125, "5")
                .put(126, "6")
                .put(127, "7")
                .put(128, "8")
                .put(129, "9")
                .put(130, "!")
                .put(132, "#")
                .put(133, "¤")
                .put(136, "%")
                .put(145, "&")
                .put(146, "/")
                .put(148, "(")
                .put(149, ")")
                .put(151, "=")
                .put(153, "?")
                .put(154, "$")
                .put(155, "€")
                .put(156, "{")
                .put(157, "[")
                .put(158, "]")
                .put(159, "}")
                .put(160, "+")
                .put(161, "-")
                .put(162, "_")
                .put(163, ".")
                .put(164, ",")
                .put(165, ";")
                .put(166, ":")
                .put(167, "*")
                .put(170, "~")
                .put(171, "^")
                .put(172, "´")
                .put(173, "`")
                .put(174, "|")
                .put(175, "<")
                .put(176, ">")
                .put(177, "§")
                .put(178, "½")
                .put(179, "ñ")
                .put(180, "@")
                .put(181, "û")
                .put(182, "â")
                .put(183, "ê")
                .put(184, "ô")
                .put(185, "é")
                .put(186, "á")
                .put(187, "ó")
                .put(188, "ú")
                .put(189, "í")
                .put(190, "ý")
                .put(191, "è")
                .put(192, "à")
                .put(193, "ò")
                .put(194, "ù")
                .put(195, "æ")
                .put(198, "ø")
                .put(199, "å")
                .build();
    }
}
