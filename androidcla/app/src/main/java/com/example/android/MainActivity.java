package com.example.android;

import static com.example.android.R.*;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android.api.RetrofitClient;
import com.example.android.model.Quote;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvQuote;
    private TextView tvAuthor;
    private Button btnMotivate;
    private ProgressBar progressBar;
    private CardView quoteCard;
    private FloatingActionButton fabThemeToggle;
    
    // Preferencias para guardar el tema seleccionado
    private SharedPreferences sharedPreferences;
    private static final String THEME_PREFS = "theme_prefs";
    private static final String CURRENT_THEME = "current_theme";

    // Array de frases motivacionales offline
    private final String[][] offlineQuotes = {
            {"La única forma de hacer un gran trabajo es amar lo que haces.", "Steve Jobs"},
            {"El éxito no es definitivo, el fracaso no es fatal: lo que cuenta es el coraje para continuar.", "Winston Churchill"},
            {"No importa lo lento que vayas, siempre y cuando no te detengas.", "Confucio"},
            {"El futuro pertenece a quienes creen en la belleza de sus sueños.", "Eleanor Roosevelt"},
            {"La vida es 10% lo que nos sucede y 90% cómo reaccionamos a ello.", "Charles R. Swindoll"}
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar el tema guardado antes de configurar la interfaz
        applyThemeFromPreferences();
        
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configuración de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        tvQuote = findViewById(R.id.tvQuote);
        tvAuthor = findViewById(R.id.tvAuthor);
        btnMotivate = findViewById(R.id.btnMotivate);
        progressBar = findViewById(R.id.progressBar);
        quoteCard = findViewById(R.id.quoteCard);
        fabThemeToggle = findViewById(R.id.fabThemeToggle);
        
        // Actualizar el icono del FAB según el tema actual
        updateThemeToggleIcon();

        // Configurar click listener para el botón de motivación
        btnMotivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    fetchRandomQuote();
                } else {
                    animateQuoteDisplay();
                    showOfflineQuote();
                    Toast.makeText(MainActivity.this, "No hay conexión a internet. Mostrando frase offline.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Configurar click listener para el botón de cambio de tema
        fabThemeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme();
            }
        });
    }
    
    /**
     * Aplica el tema guardado en preferencias
     */
    private void applyThemeFromPreferences() {
        sharedPreferences = getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        int theme = sharedPreferences.getInt(CURRENT_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }
    
    /**
     * Cambia entre tema claro y oscuro
     */
    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int newTheme;
        
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Cambiar a tema claro
            newTheme = AppCompatDelegate.MODE_NIGHT_NO;
        } else {
            // Cambiar a tema oscuro
            newTheme = AppCompatDelegate.MODE_NIGHT_YES;
        }
        
        // Guardar la preferencia
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CURRENT_THEME, newTheme);
        editor.apply();
        
        // Aplicar el nuevo tema
        AppCompatDelegate.setDefaultNightMode(newTheme);
    }
    
    /**
     * Actualiza el icono del FAB según el tema actual
     */
    private void updateThemeToggleIcon() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Estamos en modo oscuro, mostrar icono de sol
            fabThemeToggle.setImageResource(android.R.drawable.ic_menu_day);
        } else {
            // Estamos en modo claro, mostrar icono de luna
            fabThemeToggle.setImageResource(android.R.drawable.ic_menu_mapmode);
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Actualizar el icono cuando cambie la configuración
        updateThemeToggleIcon();
    }

    /**
     * Verifica si hay conexión a internet disponible
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Muestra una frase motivacional offline aleatoria
     */
    private void showOfflineQuote() {
        int randomIndex = new Random().nextInt(offlineQuotes.length);
        String quoteText = offlineQuotes[randomIndex][0];
        String author = offlineQuotes[randomIndex][1];
        
        tvQuote.setText(quoteText);
        tvAuthor.setText("- " + author);
    }

    /**
     * Método para obtener una frase aleatoria de la API
     */
    private void fetchRandomQuote() {
        // Mostrar progreso y deshabilitar botón
        progressBar.setVisibility(View.VISIBLE);
        btnMotivate.setEnabled(false);

        // Hacer la llamada a la API
        Call<List<Quote>> call = RetrofitClient.getApiService().getRandomQuote();
        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                progressBar.setVisibility(View.GONE);
                btnMotivate.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Quote quote = response.body().get(0);
                    // Agregar animación antes de mostrar la nueva frase
                    animateQuoteDisplay();
                    displayQuote(quote);
                } else {
                    showError(getString(R.string.error_fetching_quote));
                    // Si hay error en la respuesta, mostrar frase offline
                    animateQuoteDisplay();
                    showOfflineQuote();
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnMotivate.setEnabled(true);
                showError(getString(R.string.network_error));
                // Si hay error de conexión, mostrar frase offline
                animateQuoteDisplay();
                showOfflineQuote();
            }
        });
    }

    /**
     * Método para mostrar la frase en la UI
     */
    private void displayQuote(Quote quote) {
        tvQuote.setText(quote.getQuoteText());
        tvAuthor.setText("- " + quote.getAuthor());
    }
    
    /**
     * Método para animar la aparición de una nueva frase
     */
    private void animateQuoteDisplay() {
        // Crear animación de desvanecimiento para salida
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(quoteCard, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        
        // Crear animación de movimiento para salida
        ObjectAnimator moveOut = ObjectAnimator.ofFloat(quoteCard, "translationX", 0f, -200f);
        moveOut.setDuration(300);
        
        // Crear conjunto de animaciones para salida
        AnimatorSet animSetOut = new AnimatorSet();
        animSetOut.playTogether(fadeOut, moveOut);
        animSetOut.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Crear animación de desvanecimiento para entrada
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(quoteCard, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        
        // Crear animación de movimiento para entrada
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(quoteCard, "translationX", 200f, 0f);
        moveIn.setDuration(300);
        
        // Crear conjunto de animaciones para entrada
        AnimatorSet animSetIn = new AnimatorSet();
        animSetIn.playTogether(fadeIn, moveIn);
        animSetIn.setInterpolator(new DecelerateInterpolator());
        
        // Crear secuencia completa de animación
        AnimatorSet fullAnimation = new AnimatorSet();
        fullAnimation.playSequentially(animSetOut, animSetIn);
        fullAnimation.start();
    }

    /**
     * Método para mostrar mensajes de error
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}