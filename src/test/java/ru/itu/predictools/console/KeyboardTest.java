package ru.itu.predictools.console;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class KeyboardTest {

//    private static final int NUMBER_OF_KEYS_1 = 9; //Для произволного распределения алфавита
//    private static final int CHARS_PER_KEY = 4; //Для произволного распределения алфавита
//    private static final int NUMBER_OF_DIMENSIONS = 2; //Для произволного распределения алфавита
//    private static final int NUMBER_OF_KEYS_2 = 15; //Для автоматического распределения алфавита

    private Keyboard k1, k2, k3, k4, k5;
//    private AlphabetRussian alphabet;

    @Before
    public void Init(){
//        alphabet = new AlphabetRussian();
        //Keyboard(alphabet, NUMBER_OF_DIMENSIONS, NUMBER_OF_KEYS, CHARS_PER_KEY)
//        k1 = new Keyboard(alphabet); //линейная
//        k2 = new Keyboard(alphabet, 16);//автоматически распределяемый алфавит
//        k3 = new Keyboard(alphabet, 2, 9, 4);//произволная 2d 9 клавиш по 4 символа на клавишу
//        k4 = new Keyboard(alphabet, 2, 16, 3);//произволная 2d 16 клавиш по 3 символа на клавишу
//        k5 = new Keyboard(alphabet, 2, 16, 4);//произволная 2d 16 клавиш по 4 символа на клавишу
        //TODO testing cycle for each combination of {alphabet, dimsNumber, numberOfKeys, charsPerKey} and for automatic keyboard {alphabet, numberOfKeys}
        //TODO test situation whet number of keys is not sufficient for assigned size of group of chars per key (dim=2 keys=9 charsPerKey=2) exception must be thrown
    }

    @Test
    public void checkAlphabetInitialization(){

//        assertEquals(Arrays.toString(alphabet.chars()), "[А, Б, В, Г, Д, Е, Ж, З, И, Й, К, Л, М, Н, О, П, Р, С, Т, У, Ф, Х, Ц, Ч, Ш, Щ, Ъ, Ы, Ь, Э, Ю, Я]");

        System.out.println();
        System.out.println("Проверка инициализации алфавита:");
//        System.out.print(Arrays.toString(alphabet.chars()));
        System.out.println();
    }

    @Test
    public void checkKeyInitialization(){
        Integer[] coordinates = new Integer[]{0,0};
        Key key = new Key("АБВГ", coordinates);

        assertEquals(key.getNumberOfChars(), 4);
        assertEquals(key.getKeyContent(),"АБВГ");

        System.out.println();
        System.out.println("Проверка инициализации отдельной клавиши:");
        System.out.print("Клавиша " + key.getKeyContent() + " координаты (");
        Arrays.stream(key.getCoordinates()).peek(Object::toString).map(p->p+";").forEach(System.out::print);
        System.out.println(") количество символов клавиши - " + key.getNumberOfChars());
        System.out.println();

    }

    @Test
    public void checkKeyboardInitialization(){
        String keyContent;

        System.out.println();
        System.out.println("Проверка инициализации клавиатур разных типов:");
        System.out.println(" 1) линейная клавиатура (посимвольный ввод - одна стимуляция = одна клавиша = один символ");
        System.out.println("   размер алфавита: " + k1.getAlphabet().size());
        System.out.println("   алфавит: " + Arrays.toString(k1.getAlphabet().chars()));
        System.out.print("   клавиши по буквам: "); Arrays.stream(k1.getCharKeyMap()).forEach(k->System.out.print("["+k.getKeyContent()+"]")); System.out.print("\n");
        System.out.print("   клавиши: " + k1.getKeys().length + " шт.");
        for(int i=0; i < k1.getKeys().length; i++){
            if(k1.getKeys()[i] != null) keyContent = k1.getKeys()[i].getKeyContent();
            else  keyContent="";
            System.out.print("[" + keyContent + "]");
        }
        System.out.println();
        System.out.println(" 2) многомерная клавиатура с автоматическим распределением алфавита по заданному количеству (16) клавиш (MD spelling - одна стимуляция = одно измерение = группа символов");
        System.out.println("   размер алфавита: " + k2.getAlphabet().size());
        System.out.println("   алфавит: " + Arrays.toString(k2.getAlphabet().chars()));
        System.out.print("   клавиши по буквам: "); Arrays.stream(k2.getCharKeyMap()).forEach(k->System.out.print("["+k.getKeyContent()+"]")); System.out.print("\n");
        System.out.print("   клавиши: " + k2.getKeys().length + " шт.");
        for(int i=0; i < k2.getKeys().length; i++){
            if(k2.getKeys()[i] != null) keyContent = k2.getKeys()[i].getKeyContent();
            else  keyContent="";
            System.out.print("[" + keyContent + "]");
        }
        System.out.println();
        System.out.println(" 3) произвольная двумерная клавиатура (2d 9 клавиш по 4 букв.алфавита на клавишу) ");
        System.out.println("    с ручным распределением алфавита (CR spelling - одна стимуляция = одно измерение = группа символов (строка или столбец)");
        System.out.println("   размер алфавита: " + k3.getAlphabet().size());
        System.out.println("   алфавит: " + Arrays.toString(k3.getAlphabet().chars()));
        System.out.print("   клавиши по буквам: "); Arrays.stream(k3.getCharKeyMap()).forEach(k->System.out.print("["+k.getKeyContent()+"]")); System.out.print("\n");
        System.out.print("   клавиши: " + k3.getKeys().length + " шт.");
        for(int i=0; i < k3.getKeys().length; i++){
            if(k3.getKeys()[i] != null) keyContent = k3.getKeys()[i].getKeyContent();
            else  keyContent="";
            System.out.print("[" + keyContent + "]");
        }
        System.out.println();
        System.out.println(" 4) произвольная двумерная клавиатура (2d 16 клавиш по 3 букв.алфавита на клавишу)");
        System.out.println("    с ручным распределением алфавита (CR spelling - одна стимуляция = одно измерение = группа символов (строка или столбец)");
        System.out.println("   размер алфавита: " + k4.getAlphabet().size());
        System.out.println("   алфавит: " + Arrays.toString(k4.getAlphabet().chars()));
        System.out.print("   клавиши по буквам: "); Arrays.stream(k4.getCharKeyMap()).forEach(k->System.out.print("["+k.getKeyContent()+"]")); System.out.print("\n");
        System.out.print("   клавиши: " + k4.getKeys().length + " шт.");
        for(int i=0; i < k4.getKeys().length; i++){
            if(k4.getKeys()[i] != null) keyContent = k4.getKeys()[i].getKeyContent();
            else  keyContent="";
            System.out.print("[" + keyContent + "]");
        }
        System.out.println();
        System.out.println(" 5) произвольная двумерная клавиатура (2d 16 клавиш по 4 букв.алфавита на клавишу)");
        System.out.println("    с ручным распределением алфавита (CR spelling - одна стимуляция = одно измерение = группа символов (строка или столбец)");
        System.out.println("   размер алфавита: " + k5.getAlphabet().size());
        System.out.println("   алфавит: " + Arrays.toString(k5.getAlphabet().chars()));
        System.out.print("   клавиши по буквам: "); Arrays.stream(k5.getCharKeyMap()).forEach(k->System.out.print("["+k.getKeyContent()+"]")); System.out.print("\n");
        System.out.print("   клавиши: " + k5.getKeys().length + " шт.");
        for(int i=0; i < k5.getKeys().length; i++){
            if(k5.getKeys()[i] != null) keyContent = k5.getKeys()[i].getKeyContent();
            else  keyContent="";
            System.out.print("[" + keyContent + "]");
        }
        System.out.println();
        System.out.println();

    }
}
//TODO test with multikeys (T9-like)
//todo 1. text as an input that produces the set of keys as an output
//todo 2. From groups of the multykey's symbols we should generate an output set of substrings filtered through prefix trie
//todo      (there are no errors on this stage, because this is just the result of our key-button pressings) and as a result we get set of possible prefixes
//todo 3. On the set of possible prefixes it is possible to build predictive set with possible errors or let user to select one of prefixes
//todo 4. During the input we can rearrange the set of the keyboard keys if the set of possible symbols is could be reduced (this function needs the deep consideration because
//todo      such rearrangement could produce implicit error and as a result keyboard could become broken/unusable)
//todo 5. Then on the keys that gets free or on the newly created keys we could generate keys of speed text selection (keys with predictive text)
//todo 6. So there is two roles of trie: first - to reduce dimensions (T9-like); second - fuzzy searching (correction of mistypes) (trie-levenstein)
