package fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myroom.R;
import model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupTabFragment extends Fragment {
    EditText signupEmail,username, signupPass, confirmPass;
    Button signup;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        signupEmail = root.findViewById(R.id.user_email_signup);
        username = root.findViewById(R.id.user_name_signup);
        signupPass =root.findViewById(R.id.user_password_signup);
        confirmPass = root.findViewById(R.id.user_confirmPass);
        signup = root.findViewById(R.id.button_signup);
        progressDialog = new ProgressDialog(getContext());
        signup.setOnClickListener(view -> signUpUser());
        return root;
    }



    private void signUpUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        String email = signupEmail.getText().toString().trim();
        String name = username.getText().toString().trim();
        String password = signupPass.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();
        String address="",phone="",sex="",role ="normal";
        Log.d("emailUser", email);
        Log.d("pass", password);
        if (email.isEmpty()) {
            signupEmail.setError("Bạn chưa nhập email!");
            signupEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupEmail.setError("Email không đúng!");
            signupEmail.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            username.setError("Bạn chưa nhập họ tên!");
            username.requestFocus();
            return;
        }
        if(password.length()<8||password.length()>16){
            signupPass.setError("Mật khẩu cần dài từ 8-16 kí tự");
            signupPass.requestFocus();
            return;
        }
        if(!checknum(password)){
            signupPass.setError("Mật khẩu cần ít nhất 1 số");
            signupPass.requestFocus();
            return;
        }
        if(!check1lowercase(password)){
            signupPass.setError("Mật khẩu cần ít nhất 1 chữ thường");
            signupPass.requestFocus();
            return;
        }
        if(checkWhiteSpace(password)){
            signupPass.setError("Mật khẩu không được chứa khoảng trắng");
            signupPass.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPass.setError("Bạn cần xác nhận mật khẩu!");
            confirmPass.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword )) {
            confirmPass.setError("Mật khẩu xác nhận không đúng");
            confirmPass.requestFocus();
            return;
        }
        progressDialog.setMessage("Đang đăng kí...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(email, name, password,sex,phone,address,role);
                        Log.d("user", user.toString());
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        progressDialog.cancel();
                                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        user1.sendEmailVerification();
                                        Toast.makeText(getContext(), "Chúc mừng bạn đã đăng kí thành công! vui lòng kiểm tra email để xác nhận", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.cancel();
                                        Toast.makeText(getContext(), "Đăng kí thất bại, vui lòng thử lại!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        progressDialog.cancel();
                        Toast.makeText(getContext(), "Đăng kí thất bại, vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                        Log.d("Error", Objects.requireNonNull(task.getException()).toString());
                    }
                });

    }
    public static boolean checknum(String s) {
        String pattern = ".*\\d.*";
        return s.matches(pattern);
    }
    public static boolean check1lowercase(String s) {
        String pattern = ".*\\w.*";
        return s.matches(pattern);
    }
    public static boolean checkWhiteSpace(String s) {
        return s.contains(" ");
    }
}
