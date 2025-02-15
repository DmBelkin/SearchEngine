package searchengine.utils;

public class CalculateLemmaRankByPage {


    public float KMPCalculateRank(String pattern, String content) {
        //алгоритм поиска подстроки Кнута-Мориса-Пратта
        float k = 1;
        int m = pattern.length();
        int n = content.length();
        int[] lps = computePrefix(pattern);
        int j = 0;
        int i = 0;
        while (i < n) {
            if (pattern.charAt(j) == content.charAt(i)) {
                j++;
                i++;
            }
            if (m - j == 0) {
                k++;
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != content.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }
        return k;
    }

    public int[] computePrefix(String input) {
        int[] prefix = new int[input.length()];
        int j;
        for (int i = 1; i < input.length(); i++) {
            j = prefix[i - 1];
            while (j > 0 && input.charAt(j) != input.charAt(i)) {
                j = prefix[j - 1];
            }
            if (input.charAt(j) == input.charAt(i)) {
                j += 1;
            }
            prefix[i] = j;
        }
        return prefix;
    }


}
