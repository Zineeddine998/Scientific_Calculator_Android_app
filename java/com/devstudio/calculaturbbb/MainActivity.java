package com.devstudio.calculaturbbb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity {
    public static String MY_PREFS = "MY_PREFS";
    private static final String TAG = "MainActivity";
    Button acButton;
    Button answerButton;
    Button backspaceButton;
    Button closeParenthesisButton;
    public String[] commands;
    Button cosineButton;
    Button decimalButton;
    Button degButton;
    TextView displayResult;
    TextView displayText;
    Button divideButton;
    Button eButton;
    Button equalButton;
    Button expButton;
    Button factorialButton;
    List<String[]> history_list = new ArrayList();
    public boolean justEvaluated = false;
    public String lastAnswer = "";
    Button lnButton;
    Button logButton;
    SwipeMenuListView lv;
    private SlidingUpPanelLayout mLayout;
    Button multiplyButton;
    Button number0Button;
    Button number1Button;
    Button number2Button;
    Button number3Button;
    Button number4Button;
    Button number5Button;
    Button number6Button;
    Button number7Button;
    Button number8Button;
    Button number9Button;
    Button openParenthesisButton;
    Button percentButton;
    Button piButton;
    Button plusButton;
    Button plusoumoinsButton;
    int prefMode = 0;
    Button radButton;
    Button reciprocalButton;
    public String[] results;
    private SharedPreferences sharedPref;
    ImageView show_hide_history_icon;
    TextView show_hide_history_text;
    Button sineButton;
    Button squareRootButton;
    Button subtractButton;
    Button tangentButton;
    Button xyButton;

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getApplicationContext().getResources().getDisplayMetrics());
    }

    private void overrideFonts(Context context, View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    overrideFonts(context, vg.getChildAt(i));
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Play-Regular.ttf"));
            }
        } catch (Exception e) {
        }
    }

    public void checkEvaluatedState() {
        if (this.justEvaluated) {
            this.displayText.setText("");
            this.displayResult.setText("");
            this.justEvaluated = false;
        }
    }

    public void checkFirstOperation() {
        if (this.justEvaluated) {
            this.displayText.setText(this.lastAnswer);
            this.displayResult.setText("");
            this.justEvaluated = false;
        }
    }

    public String[] getlastnumber() {
        String[] found = new String[]{"", ""};
        String text = this.displayText.getText().toString();
        Matcher match = Pattern.compile("(\\d+)+$").matcher(text);
        while (match.find()) {
            found[0] = match.group();
            found[1] = text.substring(0, text.length() - found[0].length());
            System.out.println("group 0 :" + found[0]);
            System.out.println("group 1 :" + found[1]);
        }
        match = Pattern.compile("(-|\\+)(\\d+)+$").matcher(text);
        while (match.find()) {
            found[0] = match.group();
            found[1] = text.substring(0, text.length() - found[0].length());
            System.out.println("group 0 :" + found[0]);
            System.out.println("group 1 :" + found[1]);
        }
        match = Pattern.compile("(\\d+)*(\\.)(\\d+)+$").matcher(text);
        while (match.find()) {
            found[0] = match.group();
            found[1] = text.substring(0, text.length() - found[0].length());
            System.out.println("group 0 :" + found[0]);
            System.out.println("group 1 :" + found[1]);
        }
        match = Pattern.compile("(-|\\+)(\\d+)*(\\.)(\\d+)+$").matcher(text);
        while (match.find()) {
            found[0] = match.group();
            found[1] = text.substring(0, text.length() - found[0].length());
            System.out.println("group 0 :" + found[0]);
            System.out.println("group 1 :" + found[1]);
        }
        return found;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isOperatorPossible() {
        String lastchar = "";
        if (this.displayText.getText().length() > 0) {
            lastchar = String.valueOf(this.displayText.getText().charAt(this.displayText.getText().length() - 1));
            System.out.println("last char :" + lastchar);
            if (lastchar.matches("^[!%)0-9]+$")) {
                System.out.println("false match");
                return true;
            }
            System.out.println("true dont match");
            return false;
        }
        System.out.println("false 0");
        return false;
    }

    public boolean isopenparenthesePossible() {
        String lastchar = "";
        if (this.displayText.getText().length() > 0) {
            lastchar = String.valueOf(this.displayText.getText().charAt(this.displayText.getText().length() - 1));
            System.out.println("last char :" + lastchar);
            if (lastchar.matches("^[^!%)0-9]+$")) {
                System.out.println("false match");
                return true;
            }
            System.out.println("true dont match");
            return false;
        }
        System.out.println("false 0");
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        this.sharedPref = getSharedPreferences(MY_PREFS, this.prefMode);
        Typeface font = Typeface.createFromAsset(getAssets(), "Play-Regular.ttf");
        overrideFonts(this, findViewById(R.id.sliding_layout));
        this.displayText = (TextView) findViewById(R.id.displayText);
        this.displayText.setTypeface(font);
        this.displayResult = (TextView) findViewById(R.id.displayResult);
        this.displayResult.setTypeface(font);
        this.acButton = (Button) findViewById(R.id.acButton);
        this.acButton.setTypeface(font);
        this.equalButton = (Button) findViewById(R.id.equalButton);
        this.percentButton = (Button) findViewById(R.id.percentButton);
        this.factorialButton = (Button) findViewById(R.id.factorialButton);
        this.decimalButton = (Button) findViewById(R.id.decimalButton);
        this.plusoumoinsButton = (Button) findViewById(R.id.plusoumoinsButton);
        this.plusButton = (Button) findViewById(R.id.plusButton);
        this.subtractButton = (Button) findViewById(R.id.subtractButton);
        this.multiplyButton = (Button) findViewById(R.id.multiplyButton);
        this.divideButton = (Button) findViewById(R.id.divideButton);
        this.number9Button = (Button) findViewById(R.id.number9Button);
        this.number8Button = (Button) findViewById(R.id.number8Button);
        this.number7Button = (Button) findViewById(R.id.number7Button);
        this.number6Button = (Button) findViewById(R.id.number6Button);
        this.number5Button = (Button) findViewById(R.id.number5Button);
        this.number4Button = (Button) findViewById(R.id.number4Button);
        this.number3Button = (Button) findViewById(R.id.number3Button);
        this.number2Button = (Button) findViewById(R.id.number2Button);
        this.number1Button = (Button) findViewById(R.id.number1Button);
        this.number0Button = (Button) findViewById(R.id.number0Button);
        this.backspaceButton = (Button) findViewById(R.id.backspaceButton);
        this.radButton = (Button) findViewById(R.id.radButton);
        this.degButton = (Button) findViewById(R.id.degButton);
        this.squareRootButton = (Button) findViewById(R.id.squareRootButton);
        this.xyButton = (Button) findViewById(R.id.xyButton);
        this.expButton = (Button) findViewById(R.id.expButton);
        this.lnButton = (Button) findViewById(R.id.lnButton);
        this.eButton = (Button) findViewById(R.id.eButton);
        this.logButton = (Button) findViewById(R.id.logButton);
        this.reciprocalButton = (Button) findViewById(R.id.reciprocalButton);
        this.piButton = (Button) findViewById(R.id.piButton);
        this.sineButton = (Button) findViewById(R.id.sineButton);
        this.cosineButton = (Button) findViewById(R.id.cosineButton);
        this.tangentButton = (Button) findViewById(R.id.tangentButton);
        this.openParenthesisButton = (Button) findViewById(R.id.openParenthesisButton);
        this.answerButton = (Button) findViewById(R.id.answerButton);
        ArrayList<Button> buttons = new ArrayList();
        buttons.add(this.percentButton);
        buttons.add(this.plusoumoinsButton);
        buttons.add(this.decimalButton);
        buttons.add(this.plusButton);
        buttons.add(this.subtractButton);
        buttons.add(this.multiplyButton);
        buttons.add(this.divideButton);
        buttons.add(this.number9Button);
        buttons.add(this.number8Button);
        buttons.add(this.number7Button);
        buttons.add(this.number6Button);
        buttons.add(this.number5Button);
        buttons.add(this.number4Button);
        buttons.add(this.number3Button);
        buttons.add(this.number2Button);
        buttons.add(this.number1Button);
        buttons.add(this.number0Button);
        ArrayList<Button> scientificButtons = new ArrayList();
        scientificButtons.add(this.radButton);
        scientificButtons.add(this.degButton);
        scientificButtons.add(this.squareRootButton);
        scientificButtons.add(this.xyButton);
        scientificButtons.add(this.expButton);
        scientificButtons.add(this.lnButton);
        scientificButtons.add(this.eButton);
        scientificButtons.add(this.logButton);
        scientificButtons.add(this.reciprocalButton);
        scientificButtons.add(this.piButton);
        scientificButtons.add(this.sineButton);
        scientificButtons.add(this.cosineButton);
        scientificButtons.add(this.tangentButton);
        scientificButtons.add(this.openParenthesisButton);
        scientificButtons.add(this.answerButton);
        Iterator it = buttons.iterator();
        while (it.hasNext()) {
            ((Button) it.next()).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String lastchar;
                    switch (view.getId()) {
                        case R.id.percentButton /*2131492958*/:
                            MainActivity.this.checkFirstOperation();
                            lastchar = "";
                            if (MainActivity.this.displayText.getText().length() > 0) {
                                lastchar = String.valueOf(MainActivity.this.displayText.getText().charAt(MainActivity.this.displayText.getText().length() - 1));
                            }
                            if (MainActivity.this.isOperatorPossible() && !lastchar.equals(MainActivity.this.percentButton.getText())) {
                                MainActivity.this.displayText.append(MainActivity.this.percentButton.getText());
                                return;
                            }
                            return;
                        case R.id.plusoumoinsButton /*2131492959*/:
                            MainActivity.this.checkFirstOperation();
                            String[] lastnumber = MainActivity.this.getlastnumber();
                            if (!lastnumber[0].equals("")) {
                                if (lastnumber[1].equals("")) {
                                    if ((Double.parseDouble(lastnumber[0]) * -1.0d) % 1.0d == 0.0d) {
                                        MainActivity.this.displayText.setText("" + (Long.parseLong(lastnumber[0]) * -1));
                                        return;
                                    } else {
                                        MainActivity.this.displayText.setText("" + (Double.parseDouble(lastnumber[0]) * -1.0d));
                                        return;
                                    }
                                } else if (Double.parseDouble(lastnumber[0]) * -1.0d > 0.0d) {
                                    if ((Double.parseDouble(lastnumber[0]) * -1.0d) % 1.0d == 0.0d) {
                                        MainActivity.this.displayText.setText(lastnumber[1] + "+" + (Long.parseLong(lastnumber[0]) * -1));
                                        return;
                                    } else {
                                        MainActivity.this.displayText.setText(lastnumber[1] + "+" + (Double.parseDouble(lastnumber[0]) * -1.0d));
                                        return;
                                    }
                                } else if ((Double.parseDouble(lastnumber[0]) * -1.0d) % 1.0d == 0.0d) {
                                    MainActivity.this.displayText.setText(lastnumber[1] + (Long.parseLong(lastnumber[0]) * -1));
                                    return;
                                } else {
                                    MainActivity.this.displayText.setText(lastnumber[1] + (Double.parseDouble(lastnumber[0]) * -1.0d));
                                    return;
                                }
                            }
                            return;
                        case R.id.divideButton /*2131492960*/:
                            MainActivity.this.checkFirstOperation();
                            if (MainActivity.this.isOperatorPossible()) {
                                MainActivity.this.displayText.append("/");
                                return;
                            }
                            return;
                        case R.id.number7Button /*2131492961*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number7Button.getText());
                            return;
                        case R.id.number8Button /*2131492962*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number8Button.getText());
                            return;
                        case R.id.number9Button /*2131492963*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number9Button.getText());
                            return;
                        case R.id.multiplyButton /*2131492964*/:
                            MainActivity.this.checkFirstOperation();
                            if (MainActivity.this.isOperatorPossible()) {
                                MainActivity.this.displayText.append("x");
                                return;
                            }
                            return;
                        case R.id.number4Button /*2131492965*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number4Button.getText());
                            return;
                        case R.id.number5Button /*2131492966*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number5Button.getText());
                            return;
                        case R.id.number6Button /*2131492967*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number6Button.getText());
                            return;
                        case R.id.subtractButton /*2131492968*/:
                            MainActivity.this.checkFirstOperation();
                            if (MainActivity.this.isOperatorPossible()) {
                                MainActivity.this.displayText.append(MainActivity.this.subtractButton.getText());
                                return;
                            }
                            return;
                        case R.id.number1Button /*2131492969*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number1Button.getText());
                            return;
                        case R.id.number2Button /*2131492970*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number2Button.getText());
                            return;
                        case R.id.number3Button /*2131492971*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number3Button.getText());
                            return;
                        case R.id.plusButton /*2131492972*/:
                            MainActivity.this.checkFirstOperation();
                            if (MainActivity.this.isOperatorPossible()) {
                                MainActivity.this.displayText.append(MainActivity.this.plusButton.getText());
                                return;
                            }
                            return;
                        case R.id.number0Button /*2131492973*/:
                            MainActivity.this.checkEvaluatedState();
                            MainActivity.this.displayText.append(MainActivity.this.number0Button.getText());
                            return;
                        case R.id.decimalButton /*2131492974*/:
                            MainActivity.this.checkEvaluatedState();
                            if (MainActivity.this.isOperatorPossible()) {
                                MainActivity.this.displayText.append(MainActivity.this.decimalButton.getText());
                                return;
                            }
                            return;
                        case R.id.factorialButton /*2131492987*/:
                            MainActivity.this.checkFirstOperation();
                            lastchar = String.valueOf(MainActivity.this.displayText.getText().charAt(MainActivity.this.displayText.getText().length() - 1));
                            if (MainActivity.this.isOperatorPossible() && !lastchar.equals(MainActivity.this.factorialButton.getText())) {
                                MainActivity.this.displayText.append(MainActivity.this.factorialButton.getText());
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                }
            });
        }
        if (this.backspaceButton != null) {
            this.backspaceButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (MainActivity.this.displayText.getText().length() > 0) {
                        MainActivity.this.displayText.setText(MainActivity.this.displayText.getText().toString().substring(0, MainActivity.this.displayText.length() - 1));
                    }
                }
            });
        }
        it = scientificButtons.iterator();
        while (it.hasNext()) {
            Button button = (Button) it.next();
            if (button != null) {
                button.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.radButton /*2131492981*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append("(pi/180)");
                                return;
                            case R.id.degButton /*2131492982*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append("(180/pi)");
                                return;
                            case R.id.squareRootButton /*2131492983*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append("sqrt(");
                                return;
                            case R.id.openParenthesisButton /*2131492984*/:
                                MainActivity.this.checkEvaluatedState();
                                if (MainActivity.this.isopenparenthesePossible()) {
                                    MainActivity.this.displayText.append("(");
                                    return;
                                } else {
                                    MainActivity.this.displayText.append(")");
                                    return;
                                }
                            case R.id.piButton /*2131492985*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append("pi");
                                return;
                            case R.id.sineButton /*2131492986*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.sineButton.getText() + "(");
                                return;
                            case R.id.reciprocalButton /*2131492988*/:
                                MainActivity.this.checkFirstOperation();
                                if (MainActivity.this.isOperatorPossible()) {
                                    MainActivity.this.displayText.setText("1/" + MainActivity.this.displayText.getText());
                                    return;
                                }
                                return;
                            case R.id.cosineButton /*2131492989*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.cosineButton.getText() + "(");
                                return;
                            case R.id.lnButton /*2131492990*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.lnButton.getText());
                                return;
                            case R.id.eButton /*2131492991*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.eButton.getText());
                                return;
                            case R.id.tangentButton /*2131492992*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.tangentButton.getText() + "(");
                                return;
                            case R.id.logButton /*2131492993*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.logButton.getText());
                                return;
                            case R.id.answerButton /*2131492994*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.lastAnswer);
                                return;
                            case R.id.expButton /*2131492995*/:
                                MainActivity.this.checkEvaluatedState();
                                MainActivity.this.displayText.append(MainActivity.this.expButton.getText() + "(");
                                return;
                            case R.id.xyButton /*2131492996*/:
                                MainActivity.this.checkFirstOperation();
                                if (MainActivity.this.isOperatorPossible()) {
                                    MainActivity.this.displayText.append("^");
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                    }
                });
            }
        }
        this.acButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.displayText.setText("");
                MainActivity.this.displayResult.setText("");
            }
        });
        this.equalButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.displayText.getText().length() > 0) {
                    Parser p = new ArityParser();
                    String expression = MainActivity.this.displayText.getText().toString().replace("x", "*").replace("EXP(", "exp(");
                    System.out.println("expression :" + expression);
                    String result = p.parse(expression);
                    MainActivity.this.lastAnswer = result;
                    MainActivity.this.justEvaluated = true;
                    MainActivity.this.displayResult.setText('=' + result);
                    String historyCommand = MainActivity.this.sharedPref.getString(MainActivity.this.getResources().getString(R.string.commands), "");
                    String historyResult = MainActivity.this.sharedPref.getString(MainActivity.this.getResources().getString(R.string.results), "");
                    MainActivity.this.commands = historyCommand.split(";");
                    MainActivity.this.results = historyResult.split("#");
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb_result = new StringBuilder();
                    for (int i = 0; i < MainActivity.this.commands.length; i++) {
                        sb.append(MainActivity.this.commands[i]).append(";");
                        sb_result.append(MainActivity.this.results[i]).append("#");
                    }
                    sb.append(MainActivity.this.displayText.getText().toString().replace("*", "x")).append(";");
                    sb_result.append('=' + result).append("#");
                    System.out.println("command =: " + sb.toString());
                    System.out.println("result =: " + sb_result.toString());
                    Editor editor = MainActivity.this.sharedPref.edit();
                    editor.putString(MainActivity.this.getResources().getString(R.string.commands), sb.toString());
                    editor.putString(MainActivity.this.getResources().getString(R.string.results), sb_result.toString());
                    editor.commit();
                }
            }
        });
        if (isNetworkAvailable(this)) {
            LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.admob);
            LinearLayout adContainer = new LinearLayout(this);
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId("ca-app-pub-4819829228315791/7996140099");
            adView.loadAd(new Builder().build());
            adContainer.addView(adView, new LayoutParams(-2, -2));
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(-2, -1);
            relativeParams.addRule(12, -1);
            relativeParams.addRule(13, -1);
            relativeLayout.addView(adContainer);
        }
        this.lv = (SwipeMenuListView) findViewById(R.id.lv);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(MainActivity.this.getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(201, 201, 206)));
                openItem.setWidth(MainActivity.this.dp2px(90));
                openItem.setIcon((int) R.drawable.ic_action_calculate);
                openItem.setTitleColor(-1);
                menu.addMenuItem(openItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this.getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(249, 63, 37)));
                deleteItem.setWidth(MainActivity.this.dp2px(90));
                deleteItem.setIcon((int) R.drawable.ic_action_trash);
                menu.addMenuItem(deleteItem);
            }
        };
        this.show_hide_history_text = (TextView) findViewById(R.id.show_hide_history_text);
        this.show_hide_history_icon = (ImageView) findViewById(R.id.show_hide_history_icon);
        this.mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        this.mLayout.addPanelSlideListener(new PanelSlideListener() {
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(MainActivity.TAG, "onPanelSlide, offset " + slideOffset);
            }

            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(MainActivity.TAG, "onPanelStateChanged " + newState);
                String historyCommand = MainActivity.this.sharedPref.getString(MainActivity.this.getString(R.string.commands), "");
                MainActivity.this.commands = historyCommand.replaceFirst("^;", "").split(";");
                String historyResult = MainActivity.this.sharedPref.getString(MainActivity.this.getString(R.string.results), "");
                MainActivity.this.results = historyResult.replaceFirst("^#", "").split("#");
                System.out.println("command array : " + historyCommand);
                System.out.println("result array : " + historyResult);
                if (MainActivity.this.commands.length > 0) {
                    ArrayList<HashMap<String, String>> historyItems = new ArrayList();
                    for (int i = 0; i < MainActivity.this.commands.length; i++) {
                        HashMap<String, String> historyItem = new HashMap();
                        historyItem.put("command", MainActivity.this.commands[i]);
                        historyItem.put("result", MainActivity.this.results[i]);
                        System.out.println("command : " + MainActivity.this.commands[i]);
                        System.out.println("result : " + MainActivity.this.results[i]);
                        historyItems.add(historyItem);
                    }
                    MainActivity.this.lv.setAdapter(new SimpleAdapter(MainActivity.this.getApplicationContext(), historyItems, R.layout.history_list, new String[]{"command", "result"}, new int[]{R.id.textView1, R.id.textView2}));
                }
                MainActivity.this.lv.refreshDrawableState();
                MainActivity.this.lv.setTranscriptMode(2);
                MainActivity.this.lv.setStackFromBottom(true);
                if (MainActivity.this.mLayout.getPanelState().equals(PanelState.EXPANDED)) {
                    MainActivity.this.show_hide_history_text.setText(MainActivity.this.getResources().getString(R.string.hide_history));
                    MainActivity.this.show_hide_history_icon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.ic_arrow_up));
                    return;
                }
                MainActivity.this.show_hide_history_text.setText(MainActivity.this.getResources().getString(R.string.view_history));
                MainActivity.this.show_hide_history_icon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.ic_arrow_down));
            }
        });
        this.mLayout.setFadeOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });
        this.lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MainActivity.this.mLayout.setPanelState(PanelState.COLLAPSED);
                MainActivity.this.sharedPref = MainActivity.this.getSharedPreferences(MainActivity.MY_PREFS, MainActivity.this.prefMode);
                String[] commandsOpen = MainActivity.this.sharedPref.getString(MainActivity.this.getString(R.string.commands), "").split(";");
                String[] resultOpen = MainActivity.this.sharedPref.getString(MainActivity.this.getString(R.string.results), "").split("#");
                if (commandsOpen[0].equals("")) {
                    position++;
                }
                MainActivity.this.displayText.setText(commandsOpen[position]);
                MainActivity.this.displayResult.setText(resultOpen[position]);
            }
        });
        this.lv.setDivider(null);
        ((Button) findViewById(R.id.bClearHisory)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                MainActivity.this.sharedPref = MainActivity.this.getSharedPreferences(MainActivity.MY_PREFS, MainActivity.this.prefMode);
                Editor editor = MainActivity.this.sharedPref.edit();
                editor.putString(MainActivity.this.getString(R.string.commands), ";".toString());
                editor.putString(MainActivity.this.getString(R.string.results), "#".toString());
                editor.commit();
                String historyCommand = MainActivity.this.sharedPref.getString(MainActivity.this.getString(R.string.commands), "");
                String historyResult = MainActivity.this.sharedPref.getString(MainActivity.this.getString(R.string.results), "");
                MainActivity.this.commands = historyCommand.replaceFirst("^;", "").split(";");
                MainActivity.this.results = historyResult.replaceFirst("^#", "").split("#");
                ArrayList<HashMap<String, Object>> historyItems = new ArrayList();
                HashMap<String, Object> historyItem = new HashMap();
                for (int i = 0; i < MainActivity.this.commands.length; i++) {
                    historyItem.put("command", MainActivity.this.commands[i]);
                    historyItem.put("result", MainActivity.this.results[i]);
                    historyItems.add(historyItem);
                }
                MainActivity.this.lv.setAdapter(new SimpleAdapter(MainActivity.this.getApplicationContext(), historyItems, R.layout.history_list, new String[]{"command", "result"}, new int[]{R.id.textView1, R.id.textView2}));
                MainActivity.this.lv.refreshDrawableState();
            }
        });
        overrideFonts(this, findViewById(R.id.lv));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
