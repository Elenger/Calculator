package com.eleng.elengcalc3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eleng.elengcalc3.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //объявляю элементы экрана
    TextView mTextView;

    Button m0;
    Button m1;
    Button m2;
    Button m3;
    Button m4;
    Button m5;
    Button m6;
    Button m7;
    Button m8;
    Button m9;
    Button mDot;
    Button mEqually;
    Button mDEL;
    Button mDivide;
    Button mMultiply;
    Button mAdd;
    Button mDeduct;

    Button mOpenParentheses;
    Button mClosedParentheses;

    String mResult = "";
    String mResultSave = "";

    ArrayList mArrayList = new ArrayList();

    boolean stop = true;//переменная стопорит программу через if. Добавлена для DiscloseParentheses() - в случае если скобки расставлены неверно, вывести toast и остановить "выполнение арифметических действий" не выходя из программы.
    boolean stopDiscloseParentheses = true; //переменная для цикла while применимого в Equally к DiscloseParentheses()


    //"раскрываю" скобки
    public void DiscloseParentheses(){
        //делаю возможными записи по типу 5(2)=10 и (5)2=10
        for (int counter = 1; counter < mResult.length()-1; counter++){//если перед ( стоит число, то вставить между ними ×
            if (mResult.charAt(counter) == '(' && mResult.charAt(counter-1) != '.' && mResult.charAt(counter-1) != '÷'
                    && mResult.charAt(counter-1) != '×' && mResult.charAt(counter-1) != '-' && mResult.charAt(counter-1) != '+'){
                mResult = mResult.substring(0, counter) + "×" + mResult.substring(counter, mResult.length());
            }//иначе если после ) стоит число, то вставить между ними ×
            else if (mResult.charAt(counter) == ')' && mResult.charAt(counter+1) != ')' && mResult.charAt(counter+1) != '.' && mResult.charAt(counter+1) != '÷'
                    && mResult.charAt(counter+1) != '×' && mResult.charAt(counter+1) != '-' && mResult.charAt(counter+1) != '+')
                mResult = mResult.substring(0, counter+1) + "×" + mResult.substring(counter+1, mResult.length());
        }

        //проверяю верно ли расставлены скобки
        int amountOpen = 0;//в переменной содержится количество (
        int amountClose = 0;//в переменной содержится количество )
        int lastOpen = 0;//переменная содержит номер последней (
        int lastClose = 0;//переменная содержит номер последней )

        for (int countOpen = 0; countOpen <= mResult.length()-1; countOpen++){
            char CharCountOpen = mResult.charAt(countOpen); //выясняю символ под номером counter
            if (CharCountOpen == '('){//если это ( тогда
                amountOpen++;//количество ( +1
                lastOpen = countOpen;
            }
        }
        for (int countClose = 0; countClose <= mResult.length()-1; countClose++){
            char CharCountClose = mResult.charAt(countClose); //выясняю символ под номером counter
            if (CharCountClose == ')'){//если это ( тогда
                amountClose++;//количество ) +1
                lastClose = countClose;
            }
        }

        //раскрываю скобки
        if (amountOpen==amountClose && lastClose > lastOpen) {//если количество ( и ) совпадает, а также все скобки закрыты
            for (int counter = mResult.length()-1; counter >= 0; counter--){//считаю от конца строки к началу
                char mOpenParentheses = mResult.charAt(counter);//выясняю символ под номером counter
                if (mOpenParentheses == '('){//если это (
                    for (int counter2 = counter; counter < mResult.length()-1; counter2++){//то иду от символа counter2, то есть (, к концу
                        char mClosedParentheses = mResult.charAt(counter2);//выясняю символ под номером counter2
                        if (mClosedParentheses == ')'){//если это ) то
                            mResultSave = mResult;
                            mResult = mResult.substring(counter+1, counter2);//mResult = строка, бывшая в скобках
                            BreakIntoOperandsAndNumbers();
                            CombineInOneElementSignsPlusOrMinusAndNumbers();
                            PerformMultiplicationAndDivision();
                            PerformAdditionAndDeduction();
                            mResultSave = mResultSave.substring(0, counter) + mResult + mResultSave.substring(counter2+1, mResultSave.length());//mResultSave = первоначальный пример с открытыми скобками
                            mResult = mResultSave;
                            counter = 0;
                            counter2 = 0;
                            stopDiscloseParentheses = mResult.contains("(");//проверяет есть ли скобка. Метод DiscloseParentheses() будет выполняться пока stopDiscloseParentheses будет true
                            break;
                        }
                    }
                }
            }
        }
        else if (lastClose==0 && lastOpen==0) {//если скобок нет, то выключить цикл while для DiscloseParentheses()
            stopDiscloseParentheses = false;
        }
        else {//если количество ( и ) не совпадает, то
            Toast toast = Toast.makeText(MainActivity.this, "Пожалуйста, проверьте скобки", Toast.LENGTH_LONG);
            toast.show();
            stopDiscloseParentheses = false; //В случае, если количество ( и ) не совпадает, то выключить цикл while для DiscloseParentheses()
            stop=false;//В случае, если количество ( и ) не совпадает, то stop = false, это не позволит программе выполнять арифметические действия (до устранения проблемы со скобками)
        }
    }

    //разбиваю mResult на массив из чисел и операндов
    public void BreakIntoOperandsAndNumbers() {
        for (int counter = 0; counter < mResult.length(); counter++) {
            char mCounterChar = mResult.charAt(counter);//выясняю символ под номером counter
            if (mCounterChar == '×' || mCounterChar == '÷' || mCounterChar == '+' || mCounterChar == '-' && counter!=0) {//если это операнд и он не первый символ в строке
                mArrayList.add(mResult.substring(0, counter));//добавляю в отдельный элемент массива всё, что перед операндом
                mArrayList.add(mResult.substring(counter, counter+1));//добавляю в отдельный элемент массива операнд
                mResult = mResult.substring(counter+1, mResult.length());//убираю все занесенные в массив символы из mResult
                counter = 0;
            }
            else if (mCounterChar == '×' || mCounterChar == '÷' || mCounterChar == '+' || mCounterChar == '-') { //если это операнд и он первый элемент в строке
                mArrayList.add(mResult.substring(counter, counter+1));//добавляю в отдельный элемент массива операнд
                mResult = mResult.substring(counter+1, mResult.length());//убираю все занесенные в массив символы из mResult
                counter = 0;
            }
        }
        mArrayList.add(mResult);//добавляю последний оставшийся кусочек (всегда число) строки mResult в массив
    }

    //соединяю в 1 элемент массива знак + или - с числом к которому он относится
    public void CombineInOneElementSignsPlusOrMinusAndNumbers() {
        for (int counter = 0; counter < mArrayList.size(); counter++) {
            String mCounterElement = (String) mArrayList.get(counter);//выясняю символ в counter элементе массива
            if (mCounterElement.equals("-") || mCounterElement.equals("+")){//если это - или +
                String mNewArrayElement = mArrayList.get(counter).toString() + mArrayList.get(counter+1).toString(); //то создаю строку, которая объеденит операнд с последующим числом
                mArrayList.set(counter, mNewArrayElement); //и заменяю этой строкой "место" операнда в массиве
                mArrayList.remove(counter+1);//последующий элемент удаляю
            }
        }
    }

    //выполняю умножение и деление
    public void PerformMultiplicationAndDivision(){
        for (int counter = 0; counter < mArrayList.size(); counter++) {
            String mCounterElement = (String) mArrayList.get(counter);//выясняю символ в counter элементе массива
            if (mCounterElement.equals("÷")) {//если это ÷
                double mNewDivisionElement = Double.valueOf((String) mArrayList.get(counter-1)) / Double.valueOf((String) mArrayList.get(counter+1));//делю элемент стоящий до операнда на элемент стоящий после операнда
                mArrayList.set(counter-1, String.valueOf(mNewDivisionElement));//записываю результат деления в элемент стоящий до операнда
                mArrayList.remove(counter+1);//удаляю элемент стоящий после операнда
                mArrayList.remove(counter);//удаляю элемент операнда
                counter--;
            }
            else if (mCounterElement.equals("×")) {
                double mNewMultiplicationElement = Double.valueOf((String) mArrayList.get(counter-1)) * Double.valueOf((String) mArrayList.get(counter+1));
                mArrayList.set(counter-1, String.valueOf(mNewMultiplicationElement));
                mArrayList.remove(counter+1);
                mArrayList.remove(counter);
                counter--;
            }
        }
    }

    //выполняю сложение и вычитание
    public void PerformAdditionAndDeduction() {
        for (int counter = 0; counter < mArrayList.size(); counter++) {
            if (mArrayList.size()>1) {//если в массиве больше 1 элемента
                double mNewAdditionElement = Double.valueOf((String) mArrayList.get(counter)) + Double.valueOf((String) mArrayList.get(counter+1));//складываю counter элемент с последующим
                mArrayList.set(counter, String.valueOf(mNewAdditionElement));//присваиваю результат от сложения(тип  String) в counter элемент массива
                mArrayList.remove(counter+1);//последующий элемент массива удаляю
                counter--;
                mResult = String.valueOf(mArrayList.get(0));
            }
            else mResult = String.valueOf(mArrayList.get(0));
        }
        mArrayList.remove(0); //после получения итогового mResult удаляю единственный элемент массива, чтобы при следующем нажатии "=" массив был, как и полагается, пустым
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //нахожу элементы экрана по идентификатору
        mTextView = (TextView) findViewById(R.id.textView);

        m0 = (Button) findViewById(R.id.button14);
        m1 = (Button) findViewById(R.id.button9);
        m2 = (Button) findViewById(R.id.button10);
        m3 = (Button) findViewById(R.id.button11);
        m4 = (Button) findViewById(R.id.button5);
        m5 = (Button) findViewById(R.id.button6);
        m6 = (Button) findViewById(R.id.button7);
        m7 = (Button) findViewById(R.id.button);
        m8 = (Button) findViewById(R.id.button2);
        m9 = (Button) findViewById(R.id.button3);
        mDot = (Button) findViewById(R.id.button13);
        mEqually = (Button) findViewById(R.id.button15);
        mDEL = (Button) findViewById(R.id.button4);
        mDivide = (Button) findViewById(R.id.button8);
        mMultiply = (Button) findViewById(R.id.button12);
        mAdd = (Button) findViewById(R.id.button17);
        mDeduct = (Button) findViewById(R.id.button16);

        mOpenParentheses = (Button) findViewById((R.id.button18));
        mClosedParentheses = (Button) findViewById((R.id.button19));


        //прописываю обработчик
        m0.setOnClickListener(MainActivity.this);
        m1.setOnClickListener(MainActivity.this);
        m2.setOnClickListener(MainActivity.this);
        m3.setOnClickListener(MainActivity.this);
        m4.setOnClickListener(MainActivity.this);
        m5.setOnClickListener(MainActivity.this);
        m6.setOnClickListener(MainActivity.this);
        m7.setOnClickListener(MainActivity.this);
        m8.setOnClickListener(MainActivity.this);
        m9.setOnClickListener(MainActivity.this);
        mDot.setOnClickListener(MainActivity.this);
        mEqually.setOnClickListener(MainActivity.this);
        mDEL.setOnClickListener(MainActivity.this);
        mDivide.setOnClickListener(MainActivity.this);
        mMultiply.setOnClickListener(MainActivity.this);
        mAdd.setOnClickListener(MainActivity.this);
        mDeduct.setOnClickListener(MainActivity.this);

        mOpenParentheses.setOnClickListener(MainActivity.this);
        mClosedParentheses.setOnClickListener(MainActivity.this);


        //длинное нажатие на DEL стирает весь текст
        mDEL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mResult = "";
                mTextView.setText(mResult);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        //определяю нажатую кнопку
        //в переменную mResult записываю "значение" этой кнопки
        switch (view.getId()) {
            case R.id.button14:
                mResult = mResult + "0";
                mTextView.setText(mResult);
                break;
            case R.id.button9:
                mResult = mResult + "1";
                mTextView.setText(mResult);
                break;
            case R.id.button10:
                mResult = mResult + "2";
                mTextView.setText(mResult);
                break;
            case R.id.button11:
                mResult = mResult + "3";
                mTextView.setText(mResult);
                break;
            case R.id.button5:
                mResult = mResult + "4";
                mTextView.setText(mResult);
                break;
            case R.id.button6:
                mResult = mResult + "5";
                mTextView.setText(mResult);
                break;
            case R.id.button7:
                mResult = mResult + "6";
                mTextView.setText(mResult);
                break;
            case R.id.button:
                mResult = mResult + "7";
                mTextView.setText(mResult);
                break;
            case R.id.button2:
                mResult = mResult + "8";
                mTextView.setText(mResult);
                break;
            case R.id.button3:
                mResult = mResult + "9";
                mTextView.setText(mResult);
                break;
            case R.id.button13:
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar != '.' && mLastChar != '÷' && mLastChar != '×' && mLastChar != '+' && mLastChar != '-') {
                        //если последний символ не ./*+- тогда добавить '.'
                        mResult = mResult + ".";
                        mTextView.setText(mResult);
                    }
                }
                break;
            case R.id.button4:
                //DEL
                if (mResult.equals("NaN") || mResult.equals("Infinity")) { //Стирать всё слово при нажатии на DEL
                    mResult = "";
                }
                else if (mResult.length() - 1 >= 0) {
                    mResult = mResult.substring(0, mResult.length() - 1);
                }
                mTextView.setText(mResult);
                //длинное нажатие реализовано в onCreate
                break;
            case R.id.button8:
                if (mResult.length() == 1) {
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ
                    if (mLastChar == '-') {
                        return;
                    }
                }
                if (mResult.length() - 2 >= 0) { //если в mTextView уже есть 2 символа, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ
                    char mPreLastChar = mResult.charAt(mResult.length() - 2);//выясняю предпоследний символ
                    if (mLastChar == '-' && mPreLastChar == '÷') {//если эти символы образают ÷-
                        mResult = mResult.substring(0, mResult.length() - 2);//то удаляю их
                        mResult = mResult + "÷";//и ставлю на их место '÷'
                        mTextView.setText(mResult);
                    } else if (mLastChar == '-' && mPreLastChar == '×') { //иначе если они образают ×-
                        mResult = mResult.substring(0, mResult.length() - 2);//то удаляю их
                        mResult = mResult + "÷";//и ставлю на их место '÷'
                        mTextView.setText(mResult);
                    }
                }
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar == '×' || mLastChar == '-' || mLastChar == '+') {//если это '÷'
                        mResult = mResult.substring(0, mResult.length() - 1);//то удаляю его
                        mResult = mResult + "÷";//и ставлю на его место '÷'
                        mTextView.setText(mResult);
                    } else if (mLastChar != '.' && mLastChar != '÷') {
                        //иначе если последний символ не .× тогда добавить '÷'
                        mResult = mResult + "÷";
                        mTextView.setText(mResult);
                    }
                }
                break;
            case R.id.button12:
                if (mResult.length() == 1) {
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ
                    if (mLastChar == '-') {
                        return;
                    }
                }
                if (mResult.length() - 2 >= 0) { //если в mTextView уже есть 2 символа, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ
                    char mPreLastChar = mResult.charAt(mResult.length() - 2);//выясняю предпоследний символ
                    if (mLastChar == '-' && mPreLastChar == '÷') {//если эти символы образают ÷-
                        mResult = mResult.substring(0, mResult.length() - 2);//то удаляю их
                        mResult = mResult + "×";//и ставлю на их место '×'
                        mTextView.setText(mResult);
                    } else if (mLastChar == '-' && mPreLastChar == '×') { //иначе если они образают ×-
                        mResult = mResult.substring(0, mResult.length() - 2);//то удаляю их
                        mResult = mResult + "×";//и ставлю на их место '×'
                        mTextView.setText(mResult);
                    }
                }
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar == '÷' || mLastChar == '-' || mLastChar == '+') {//если это '÷'
                        mResult = mResult.substring(0, mResult.length() - 1);//то удаляю его
                        mResult = mResult + "×";//и ставлю на его место '×'
                        mTextView.setText(mResult);
                    } else if (mLastChar != '.' && mLastChar != '×') {
                        //иначе если последний символ не .× тогда добавить '×'
                        mResult = mResult + "×";
                        mTextView.setText(mResult);
                    }
                }
                break;
            case R.id.button17:
                if (mResult.length() == 1) {
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ
                    if (mLastChar == '-') {
                        return;
                    }
                }
                if (mResult.length() - 2 >= 0) { //если в mTextView уже есть 2 символа, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ
                    char mPreLastChar = mResult.charAt(mResult.length() - 2);//выясняю предпоследний символ
                    if (mLastChar == '-' && mPreLastChar == '÷') {//если эти символы образают ÷-
                        mResult = mResult.substring(0, mResult.length() - 2);//то удаляю их
                        mResult = mResult + "+";//и ставлю на их место '+'
                        mTextView.setText(mResult);
                    } else if (mLastChar == '-' && mPreLastChar == '×') { //иначе если они образают ×-
                        mResult = mResult.substring(0, mResult.length() - 2);//то удаляю их
                        mResult = mResult + "+";//и ставлю на их место '+'
                        mTextView.setText(mResult);
                    }
                }
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar == '-' || mLastChar == '÷' || mLastChar == '×') {//если это / или * или -
                        mResult = mResult.substring(0, mResult.length() - 1);//то удаляю его
                        mResult = mResult + "+";//и ставлю на его место '+'
                        mTextView.setText(mResult);
                    } else if (mLastChar != '.' && mLastChar != '+') {
                        //иначе если последний символ не .+ тогда добавить '+'
                        mResult = mResult + "+";
                        mTextView.setText(mResult);
                    }
                }

                break;
            case R.id.button16:
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar == '+') {//если это '+'
                        mResult = mResult.substring(0, mResult.length() - 1);//то удаляю его
                        mResult = mResult + "-";//и ставлю на его место '-'
                        mTextView.setText(mResult);
                    } else if (mLastChar != '.' && mLastChar != '-') {
                        //иначе если последний символ не ./*- тогда добавить '-'
                        mResult = mResult + "-";
                        mTextView.setText(mResult);
                    }
                }
                else if (mResult.length()==0) {
                    mResult = "-";
                    mTextView.setText(mResult);
                }
                break;
            case R.id.button15:
                //Equally
                while (stopDiscloseParentheses) {//пока в mResult есть скобки - раскрывать их
                    DiscloseParentheses();
                }
                if (stop==true) {//если скобки расставлены верно (читай - в уравнении нет дефектов)
                    BreakIntoOperandsAndNumbers();
                    CombineInOneElementSignsPlusOrMinusAndNumbers();
                    PerformMultiplicationAndDivision();
                    PerformAdditionAndDeduction();
                    if (mResult.length() - 1 >= 1) { //если ответ заканчивается на   .0    то убрать   .0   из него
                        char mLastChar = mResult.charAt(mResult.length() - 1);
                        char mPenultChar = mResult.charAt(mResult.length() - 2);
                        if (mPenultChar == '.' && mLastChar == '0') {
                            mResult = mResult.substring(0, mResult.length() - 2);
                        }
                    }
                }
                stopDiscloseParentheses = true;
                stop=true;
                mTextView.setText(mResult);
                break;
            case R.id.button18:
                //1 скобка
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar != '.') { //если последний символ не .( ставлю '('
                        mResult = mResult + "(";
                        mTextView.setText(mResult);
                    }
                }
                else if (mResult.length()==0) {
                    mResult = "(";
                    mTextView.setText(mResult);
                }
                break;
            case R.id.button19:
                //2 скобка  /НЕЛЬЗЯ БЕЗ ОТКР
                if (mResult.length() - 1 >= 0) { //если в mTextView уже есть какой-то символ, то
                    char mLastChar = mResult.charAt(mResult.length() - 1);//выясняю последний символ mTextView
                    if (mLastChar != '.' && mLastChar != '(' && mLastChar != '÷' && mLastChar != '×' && mLastChar != '+' && mLastChar != '-') { //если последний символ не .( ставлю ')'
                        mResult = mResult + ")";
                        mTextView.setText(mResult);
                    }
                }
                break;
        }
    }
}
