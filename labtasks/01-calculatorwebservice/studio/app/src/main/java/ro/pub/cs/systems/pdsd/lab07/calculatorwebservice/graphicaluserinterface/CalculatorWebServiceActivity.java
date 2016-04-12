package ro.pub.cs.systems.pdsd.lab07.calculatorwebservice.graphicaluserinterface;

import ro.pub.cs.systems.pdsd.lab07.calculatorwebservice.R;
import ro.pub.cs.systems.pdsd.lab07.calculatorwebservice.general.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CalculatorWebServiceActivity extends Activity {
	
	private EditText operator1EditText, operator2EditText;
	private TextView resultTextView;
	private Spinner operationsSpinner, methodsSpinner;
	private String op1, op2, operation, content, method;
	
	private class CalculatorWebServiceThread extends Thread {
		
		@Override
		public void run() {
			
			// TODO: exercise 4
			// get operators 1 & 2 from corresponding edit texts (operator1EditText, operator2EditText)
			// signal missing values through error messages
			// get operation from operationsSpinner
			operator1EditText.post(new Runnable() {
				@Override
				public void run() {
				try {
					op1 = operator1EditText.getText().toString();
				} catch (Exception exception) {
					Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
					if (Constants.DEBUG) {
						exception.printStackTrace();
					}
				}
				}
			});

			operator2EditText.post(new Runnable() {
				@Override
				public void run() {
					try {
						op2 = operator2EditText.getText().toString();
					} catch (Exception exception) {
						Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
						if (Constants.DEBUG) {
							exception.printStackTrace();
						}
					}
				}
			});

			operationsSpinner.post(new Runnable() {
				@Override
				public void run() {
				try {
					operation = operationsSpinner.getSelectedItem().toString();
				} catch (Exception exception) {
					Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
					if (Constants.DEBUG) {
						exception.printStackTrace();
					}
				}
				}
			});

			methodsSpinner.post(new Runnable() {
				@Override
				public void run() {
					try {
						method = methodsSpinner.getSelectedItem().toString();
					} catch (Exception exception) {
						Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
						if (Constants.DEBUG) {
							exception.printStackTrace();
						}
					}
				}
			});

			if ((op1 == null) || (op2 == null) || (operation == null)) {
				resultTextView.post(new Runnable() {
					@Override
					public void run() {
						try {
							resultTextView.setText(content);
						} catch (Exception exception) {
							Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
							if (Constants.DEBUG) {
								exception.printStackTrace();
							}
						}
					}
				});
			} else {

				// create an instance of a HttpClient object
				HttpClient httpClient = new DefaultHttpClient();

				// get method used for sending request from methodsSpinner
				if (method.equals("GET")) {
					// 1. GET
					// a) build the URL into a HttpGet object (append the operators / operations to the Internet address)
					// b) create an instance of a ResultHandler object
					// c) execute the request, thus generating the result
					HttpGet httpGet = new HttpGet(Constants.GET_WEB_SERVICE_ADDRESS
							+ "?" + Constants.OPERATION_ATTRIBUTE + "=" + operation
							+ "&" + Constants.OPERATOR1_ATTRIBUTE + "=" + op1
							+ "&" + Constants.OPERATOR2_ATTRIBUTE + "=" + op2);
					try {
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						content = httpClient.execute(httpGet, responseHandler);
					} catch (Exception exception) {
						Log.e(Constants.TAG, exception.getMessage());
						if (Constants.DEBUG) {
							exception.printStackTrace();
						}
					}
				} else {
					// 2. POST
					// a) build the URL into a HttpPost object
					// b) create a list of NameValuePair objects containing the attributes and their values (operators, operation)
					// c) create an instance of a UrlEncodedFormEntity object using the list and UTF-8 encoding and attach it to the post request
					// d) create an instance of a ResultHandler object
					// e) execute the request, thus generating the result
					HttpPost httpPost = new HttpPost(Constants.POST_WEB_SERVICE_ADDRESS);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair(Constants.OPERATION_ATTRIBUTE, operationsSpinner.getSelectedItem().toString()));
					params.add(new BasicNameValuePair(Constants.OPERATOR1_ATTRIBUTE, op1));
					params.add(new BasicNameValuePair(Constants.OPERATOR2_ATTRIBUTE, op2));
					try {
						UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
						httpPost.setEntity(urlEncodedFormEntity);
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						content = httpClient.execute(httpPost, responseHandler);
					} catch (UnsupportedEncodingException unsupportedEncodingException) {
						Log.e(Constants.TAG, unsupportedEncodingException.getMessage());
						if (Constants.DEBUG) {
							unsupportedEncodingException.printStackTrace();
						}
					} catch (Exception exception) {
						Log.e(Constants.TAG, exception.getMessage());
						if (Constants.DEBUG) {
							exception.printStackTrace();
						}
					}
				}

				// display the result in resultTextView
				resultTextView.post(new Runnable() {
					@Override
					public void run() {
						try {
							resultTextView.setText(content);
						} catch (Exception exception) {
							Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
							if (Constants.DEBUG) {
								exception.printStackTrace();
							}
						}
					}
				});
			}
		}
	}
	
	private DisplayResultButtonClickListener displayResultButtonClickListener = new DisplayResultButtonClickListener();
	private class DisplayResultButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			new CalculatorWebServiceThread().start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator_web_service);
		
		operator1EditText = (EditText)findViewById(R.id.operator1_edit_text);
		operator2EditText = (EditText)findViewById(R.id.operator2_edit_text);
		
		resultTextView = (TextView)findViewById(R.id.result_text_view);
		
		operationsSpinner = (Spinner)findViewById(R.id.operations_spinner);
		methodsSpinner = (Spinner)findViewById(R.id.methods_spinner);
		
		Button displayResultButton = (Button)findViewById(R.id.display_result_button);
		displayResultButton.setOnClickListener(displayResultButtonClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calculator_web, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
