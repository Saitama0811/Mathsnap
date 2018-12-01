package engenoid.tessocrtest.Core.Dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import engenoid.tessocrtest.MainActivity;
import engenoid.tessocrtest.R;

public class ImageDialog extends DialogFragment {

    private Bitmap bmp;

    private String title;



    public ImageDialog(){}

    public static ImageDialog New(){
        return new ImageDialog();
    }

    public ImageDialog addBitmap(Bitmap bmp) {
        if (bmp != null)
            this.bmp = bmp;
        return this;
    }

    public ImageDialog addTitle(String title) {
        if (title != null)
            this.title = title;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_dialog, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_dialog_imageView);
        TextView textView = (TextView) view.findViewById(R.id.image_dialog_textView);

        if (bmp != null) {

            imageView.setImageBitmap(bmp);
        }

        if(title!=null){

            String result=String.valueOf(eval(title));

            textView.setText(this.title+"="+result);}

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        bmp.recycle();
        bmp = null;
        System.gc();
        super.onDismiss(dialog);
    }



    public double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length())
                {
                    gotoactivity();
                }
                return x;
            }



            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    gotoactivity();
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
    public void gotoactivity()
    {
        Toast.makeText(getActivity(),"Invalid Character",Toast.LENGTH_LONG).show();
    }
}