package com.example.projekt3;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PersonalizatorPrefs";
    private static final String KEY_COLOR_INDEX = "colorIndex";

    private ImageView bgImage;
    private TextView tvText;
    private Button btnChangeText, btnChangeColor, btnChangeBg;
    private SwitchCompat switchNight;
    private TextView tvAlphaLabel;
    private SeekBar seekAlpha;

    private final int[] images = {
            R.drawable.tlo1,
            R.drawable.tlo2,
            R.drawable.tlo3
    };

    private final int[] colors = {
            R.color.text_color,
            R.color.text_opt2,
            R.color.text_opt3
    };

    private final int[] styles = {
            Typeface.NORMAL,
            Typeface.BOLD,
            Typeface.ITALIC,
            Typeface.BOLD_ITALIC
    };

    private String[] texts;

    private int imageIndex = 0;
    private int textIndex = 0;
    private int colorIndex = 0;
    private int styleIndex = 0;
    private int textAlphaPercent = 100;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgImage = findViewById(R.id.bgImage);
        tvText = findViewById(R.id.tvText);
        btnChangeText = findViewById(R.id.btnChangeText);
        btnChangeColor = findViewById(R.id.btnChangeColor);
        btnChangeBg = findViewById(R.id.btnChangeBg);
        switchNight = findViewById(R.id.switchNight);
        tvAlphaLabel = findViewById(R.id.tvAlphaLabel);
        seekAlpha = findViewById(R.id.seekAlpha);

        texts = getResources().getStringArray(R.array.texts);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        colorIndex = prefs.getInt(KEY_COLOR_INDEX, 0) % colors.length;

        bgImage.setImageResource(images[imageIndex]);
        tvText.setText(texts[textIndex]);
        tvText.setTypeface(null, styles[styleIndex]);
        applyTextColor();

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = (currentNightMode == Configuration.UI_MODE_NIGHT_YES);
        switchNight.setChecked(isNightMode);

        btnChangeText.setOnClickListener(v -> {
            textIndex = (textIndex + 1) % texts.length;
            styleIndex = (styleIndex + 1) % styles.length;
            tvText.setText(texts[textIndex]);
            tvText.setTypeface(null, styles[styleIndex]);
        });

        btnChangeColor.setOnClickListener(v -> {
            colorIndex = (colorIndex + 1) % colors.length;
            prefs.edit().putInt(KEY_COLOR_INDEX, colorIndex).apply();
            applyTextColor();
        });

        btnChangeBg.setOnClickListener(v -> {
            imageIndex = (imageIndex + 1) % images.length;
            bgImage.setImageResource(images[imageIndex]);
        });

        switchNight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        seekAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textAlphaPercent = progress;
                tvAlphaLabel.setText("Przezroczystość tekstu: " + progress + "%");
                applyTextColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void applyTextColor() {
        int baseColor = ContextCompat.getColor(this, colors[colorIndex]);
        int alpha = textAlphaPercent * 255 / 100;
        tvText.setTextColor(ColorUtils.setAlphaComponent(baseColor, alpha));
    }
}