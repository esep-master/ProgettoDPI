package it.bleb.dpi.activities;

import androidx.appcompat.app.AppCompatActivity;
import it.bleb.dpi.R;

import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {
    public static final String EXTRA_ERROR_TITLE = "EXTRA_ERROR_TITLE";
    public static final String EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        final TextView txtTitle = findViewById(R.id.txtErrorTitle);
        final TextView txtError = findViewById(R.id.txtError);

        String title = getIntent().getStringExtra(EXTRA_ERROR_TITLE);
        if(title == null)
            title = "Unknown error";

        String message = getIntent().getStringExtra(EXTRA_ERROR_MESSAGE);
        if(message == null)
            message = "No description.";

        txtTitle.setText(title);
        txtError.setText(message);

    }
}