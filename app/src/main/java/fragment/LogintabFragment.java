package fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myroom.HomeActivity;
import com.example.myroom.R;
import com.example.myroom.ResetPassActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogintabFragment extends Fragment {
    EditText email, password;
    TextView forgetPass;
    Button login;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        email = root.findViewById(R.id.user_email);
        password = root.findViewById(R.id.user_password);
        forgetPass= root.findViewById(R.id.forgetPass);
        login = root.findViewById(R.id.button_login);
        progressDialog = new ProgressDialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();

        email.setTranslationX(800);
        password.setTranslationX(800);
        forgetPass.setTranslationX(800);
        login.setTranslationX(800);

        email.setAlpha(0);
        password.setAlpha(0);
        forgetPass.setAlpha(0);
        login.setAlpha(0);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgetPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoResetPassActivity();
            }
        });
        return root;
    }

    private void loginUser() {
        String emailUser = email.getText().toString().trim();
        String passwordUser = password.getText().toString().trim();
        if (emailUser.isEmpty()) {
            email.setError("Bạn cần nhập email!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
            email.setError("Email không đúng vui lòng kiểm tra lại!");
            email.requestFocus();
            return;
        }
        if (passwordUser.isEmpty()) {
            password.setError("Bạn chưa nhập mật khẩu!");
            password.requestFocus();
            return;
        }
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        progressDialog.cancel();
                        Intent i = new Intent(getContext(), HomeActivity.class);
                        startActivity(i);
                    } else {
                        progressDialog.cancel();
                        Toast.makeText(getContext(), "Kiểm tra email để xác nhận! Nếu bạn không thấy hãy kiểm tra thư mục spam", Toast.LENGTH_LONG).show();
                    }

                } else {
                    progressDialog.cancel();
                    Toast.makeText(getContext(), "Đăng nhập thất bại vui lòng kiểm tra lại thông tin", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void gotoResetPassActivity(){
        startActivity(new Intent(getActivity(), ResetPassActivity.class));
    }
}
