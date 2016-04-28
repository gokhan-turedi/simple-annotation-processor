package demo;


import demo.api.ToString;
import gturedi.toString.TestToStringHelper;

@ToString
public class Test {

    public String name = "Doruk T.";
    public int age = 0;

    public static void main(String[] args) {
        System.out.println("hey!");

        // sınfı olusturmak icin once projeyi build edin:
        // gradle :app:build
        // build sırasında console'a dokulen log'ları incelemeyi ihmal etmeyin
        System.out.println(TestToStringHelper.toString(new Test()));
    }

}