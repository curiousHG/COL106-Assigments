import java.io.*;
import java.util.*;

import jdk.nashorn.internal.ir.BreakableNode;

public class Anagram2 {
    static ArrayList<LinkedList<String>> hash = new ArrayList<LinkedList<String>>();
    static int vocabl;
    static int[] primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89,
            97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157 };

    public static int generateHashValue(String s) {
        long prod = 1;
        String q = "'";
        for (int i = 0; i < s.length(); i++) {
            int p = s.charAt(i) - q.charAt(0);
            if (p == 0) {
                p = primes[0];
            } else if (p < 19) {
                p = primes[p - 8];
            } else {
                p = primes[p - 47];
            }
            prod *= p;
            prod %= vocabl;

        }
        return (int) (prod);
    }

    public static long checkHash(String s) {
        long prod = 1;
        String q = "'";
        for (int i = 0; i < s.length(); i++) {
            int p = s.charAt(i) - q.charAt(0);
            if (p == 0) {
                p = primes[0];
            } else if (p < 19) {
                p = primes[p - 8];
            } else {
                p = primes[p - 47];
            }
            prod *= p;
        }
        return prod;
    }
    
    public static int count(String s){
        int h = generateHashValue(s);
        int count = 0;
        for (String p:hash.get(h)){
            if (p.equals(s)) count++;
        }
        return count;
    }

    public static void addToHash(String s) {
        int hashVal = generateHashValue(s);
        hash.get(hashVal).add(s);
    }

    public static ArrayList<String> noSpace(String s) {
        ArrayList<String> arr = new ArrayList<String>();
        int ind = generateHashValue(s);
        long val = checkHash(s);
        for (String p1 : hash.get(ind)) {
            if (val == checkHash(p1)) {
                arr.add(p1);
            }
        }
        return arr;
    }

    public static String[] stringFromCombinations(int[] combi, String s) {
        boolean[] bools = new boolean[s.length()];
        Arrays.fill(bools, Boolean.FALSE);
        for (int i : combi) {
            bools[i] = true;
        }
        String s1 = "";
        String s2 = "";
        for (int l = 0; l < s.length(); l++) {
            if (bools[l])
                s1 += s.charAt(l);
            else
                s2 += s.charAt(l);
        }
        String[] r = { s1, s2 };
        return r;
    }
    
    public static ArrayList<String[]> combinations(String s, int k) {
        ArrayList<String[]> combinations = new ArrayList<String[]>();
        int[] temp = new int[k];
        combi2(combinations, temp, 0, s.length(), 0, k,s);
        return combinations;
    }
    
    public static void combi2(ArrayList<String[]> res, int[] t, int ind, int n, int i, int r,String s) {
        if (ind == r) {
            res.add(stringFromCombinations(t, s));
            return;
        }
        if (i >= n)
            return;

        t[ind] = i;
        combi2(res, t, ind + 1, n, i + 1, r,s);
        combi2(res, t, ind, n, i + 1, r,s);
        
    }
    
    public static ArrayList<String> spaced(String s) {
        ArrayList<String> ans = new ArrayList<String>();
        for (int k = 3; k < s.length() - 2; k++) {
            for (String[] combi : combinations(s, k)){
                ArrayList<String> a1 = noSpace(combi[0]);
                ArrayList<String> a2 = noSpace(combi[1]);

                // Single Spaced
                if (a1.size() > 0 && a2.size() > 0) {
                    for (String f : a1) {
                        for (String f2 : a2) {
                            ans.add(f + " " + f2);
                        }
                    }
                }
                // Double Spaced
                if (a1.size() > 0 && combi[1].length() > 5) {
                    String half = combi[1];
                    for (int m = 3; m < half.length() - 2; m++) {
                        for (String[] combi2 : combinations(half, m)) {
                            ArrayList<String> a3 = noSpace(combi2[0]);
                            ArrayList<String> a4 = noSpace(combi2[1]);
                            if (a3.size() > 0 && a4.size() > 0) {
                                for (String f : a1) {
                                    for (String f3 : a3) {
                                        for (String f4 : a4) {
                                            ans.add(f + " " + f3 + " " + f4);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return sortRemoveRepeats(ans);
    }

    public static ArrayList<String> generateAnagrams(String s) {
        int len = s.length();
        ArrayList<String> ans = noSpace(s);
        if (len > 5) {
            ans.addAll(spaced(s));
        }
        ans = sortRemoveRepeats(ans);
        ArrayList<String> ans2 = new ArrayList<String>();
        for (String ana:ans){
            int f = ana.indexOf(" ", 0);
            int l = ana.lastIndexOf(" ");
            int a = 1;
            int b = 1;
            int c = 1;
            if(f==-1){
                a = count(ana);
            }
            else if (f==l){
                a = count(ana.substring(0, f));
                b = count(ana.substring(f+1, ana.length()));
            }else{
                a = count(ana.substring(0, f));
                b = count(ana.substring(f+1, l));
                c = count(ana.substring(l+1, ana.length()));
            }
            for(int i = 0;i<a*b*c;i++){
                // ans2.add(ana);
                System.out.println(ana);
            }
        }
        return ans2;        

    }

    public static ArrayList<String> sortRemoveRepeats(ArrayList<String> ans) {
        if (ans.size() > 0) {
            ArrayList<String> fina = new ArrayList<String>();
            ans.sort(String.CASE_INSENSITIVE_ORDER);
            fina.add(ans.get(0));
            for (int i = 1; i < ans.size(); i++) {
                if (!ans.get(i - 1).equals(ans.get(i))) {
                    fina.add(ans.get(i));
                }
            }
            return fina;
            // return ans;
        }
        return ans;

    }

    
    public static void main(String[] args) throws FileNotFoundException {
        long start = System.nanoTime();
        Scanner vocabFile = new Scanner(new File(args[0]));
        Scanner inputFile = new Scanner(new File(args[1]));
        int n = Integer.parseInt(vocabFile.nextLine());
        vocabl = n;
        for (int i = 0; i < n; i++) {
            hash.add(new LinkedList<String>());
        }
        while (vocabFile.hasNextLine()) {
            String s = vocabFile.nextLine();
            addToHash(s);
        }
        
        inputFile.nextLine();
        while (inputFile.hasNextLine()) {
            generateAnagrams(inputFile.nextLine());
            // ArrayList<String> out = generateAnagrams(inputFile.nextLine());
            // for (String anagram : out) {
            //     System.out.println(anagram);
            // }
            System.out.println(-1);
        }
        long end = System.nanoTime();
        // System.out.println((end-start)/1000);
    }
}
// Friend Name: harshkumar
// Output: hurrah mask
//         mask hurrah
