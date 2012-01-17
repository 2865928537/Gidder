package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AddUserActivity extends BaseActivity {
	private final static String TAG = AddUserActivity.class.getSimpleName();
	private static final Pattern emailPattern = Pattern.compile( "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	public final static int REQUEST_CODE_ADD_USER = 1;
	public final static int REQUEST_CODE_EDIT_USER = 2;
	
	private Button addEditButton;
	private Button cancelButton;
	private EditText fullnameEditText;
	private EditText emailEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private boolean editMode = false;
	private int userId;
	private CheckBox activateCheckox;
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_user);

		if(getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt("userId", -1);
			Log.i(TAG, "UserID: " + userId);
			
			if(userId > 0) {
				editMode = true;
			} else {
				editMode = false;
			}
		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addEditButton = (Button) findViewById(R.id.addUserBtnAddEdit);
		addEditButton.setOnClickListener(this);
		if(editMode) {
			addEditButton.setText(R.string.btn_edit);
		} else {
			addEditButton.setText(R.string.btn_add);
		}
		
		cancelButton = (Button) findViewById(R.id.addUserBtnCancel);
		cancelButton.setOnClickListener(this);
		
		fullnameEditText = (EditText) findViewById(R.id.addUserFullname);
		emailEditText = (EditText) findViewById(R.id.addUserEmail);
		usernameEditText = (EditText) findViewById(R.id.addUserUsername);
		passwordEditText = (EditText) findViewById(R.id.addUserPassword);
		activateCheckox = (CheckBox) findViewById(R.id.addUserActivate);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.addUserActionBar);
        actionBar.setHomeAction(new IntentAction(this, new Intent(this, SlideActivity.class), R.drawable.ic_actionbar_home));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.addAction(new IntentAction(this, new Intent(this, GidderPreferencesActivity.class), R.drawable.ic_actionbar_settings));

        if(editMode) {
        	actionBar.setTitle(R.string.add_user_edittitle);
        } else {
        	actionBar.setTitle(R.string.add_user_title);
        }
		
		if(editMode) {
			populateFieldsWithUserData();
		}
	}
	
	private void populateFieldsWithUserData() {
		User user = null;
		try {
			user = getHelper().getUserDao().queryForId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving user with id " + userId, e);
			return;
		}
		
		fullnameEditText.setText(user.getFullname());
		emailEditText.setText(user.getEmail());
		usernameEditText.setText(user.getUsername());
		passwordEditText.setText(user.getPassword());
		activateCheckox.setChecked(user.isActive());
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.addUserBtnAddEdit) {
			if(!isFieldsValid()) {
				return;
			}
			
			String fullname = fullnameEditText.getText().toString();
			String email = emailEditText.getText().toString();
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			boolean active = activateCheckox.isChecked();
			
			try {
				if(editMode) {
					User user = getHelper().getUserDao().queryForId(userId);
					user.setFullname(fullname);
					user.setEmail(email);
					user.setPassword(password);
					user.setUsername(username);
					user.setActive(active);
					
					getHelper().getUserDao().update(user);
				} else {
					getHelper().getUserDao().create(new User(0, fullname, email, username, password, active, System.currentTimeMillis()));
				}
			} catch (SQLException e) {
				Log.e(TAG, "Problem when add new user.", e);
				finish();
				return;
			}
			
			setResult(RESULT_OK, null);
			finish();
		} else if(v.getId() == R.id.addUserBtnCancel) {
			finish();
		}
	}
	
	private boolean isFieldsValid() {
		boolean isAllFieldsValid = true;
		
		if(!isFullnameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isEmailValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isUsernameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isPasswordValid()) {
			isAllFieldsValid = false;
		}
		
		return isAllFieldsValid;
	}
	
	private boolean isEditTextEmpty(EditText tv) {
		String text = tv.getText().toString();
		if("".equals(text.trim())) {
			tv.setError("Field must contain value");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isFullnameValid() {
		return !isEditTextEmpty(fullnameEditText);
	}
	
	private boolean isPasswordValid() {
		return !isEditTextEmpty(passwordEditText);
	}
	
	private boolean isUsernameValid() {
		return !isEditTextEmpty(usernameEditText);
	}
	
	private boolean isEmailValid() {
		if(isEditTextEmpty(emailEditText)) {
			return false;
		}
		
		String email = emailEditText.getText().toString();
		Matcher matcher = emailPattern.matcher(email);
		if(!matcher.matches()) {
			emailEditText.setError("E-mail address is incorrect");
			return false;
		}
		
		return true;
	}
	
}