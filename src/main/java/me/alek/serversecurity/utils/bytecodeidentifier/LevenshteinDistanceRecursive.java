package me.alek.serversecurity.utils.bytecodeidentifier;

public class LevenshteinDistanceRecursive {

    public int calculate(String s, String t) {
        if (s.equals(t))
            return 0;

        int n = s.length();
        int m = t.length();

        if (n == 0)
            return m;
        if (m == 0)
            return n;
        if (n > m) {
            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int[] p = new int[n + 1];
        int[] p_p = new int[n + 1];
        int[] d = new int[n + 1];
        int[] _d;
        int i;

        for (i = 0; i <= n; i++)
            p[i] = i;

        for (int j = 1; j <= m; j++) {
            char t_j = t.charAt(j - 1);
            d[0] = j;
            for (i = 1; i <= n; i++) {
                int cost = s.charAt(i - 1) == t_j ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
                        + cost);
                if (i > 1 && j > 1 && s.charAt(i - 1) == t.charAt(j - 2)
                        && s.charAt(i - 2) == t_j)
                    d[i] = Math.min(d[i], p_p[i - 2] + cost);
            }
            _d = p_p;
            p_p = p;
            p = d;
            d = _d;
        }
        return p[n];
    }
}
