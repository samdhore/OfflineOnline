package tecnodart.com.offlineonline;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String TAG="technodart";
    View finView;
    Button forget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

       // System.out.println("Technodart");//Anjali
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        forget = findViewById(R.id.forgotPasswordBtn);


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this , ForgetActivity.class));
            }
        });

    }
    protected void login(View view)
    {
        // Check if user is signed in (non-null) and update UI accordingly.
        signIn(view);
        FirebaseUser currentUser = mAuth.getCurrentUser();
       /* if(currentUser!=null)
        {
            Intent intent = new Intent(view.getContext(), Navigation.class);
            startActivity(intent);
        }*/
    }
    private void signIn(View view)
    {
        finView=view;
        EditText email=findViewById(R.id.email);
        EditText password=findViewById(R.id.password);
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Home.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(finView.getContext(), AfterLoginHome.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Home.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    protected void createAccount(View view)
    {
        Intent intent = new Intent(view.getContext(), Register.class);
        startActivity(intent);
    }

}
