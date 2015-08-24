package com.ab.health.utility;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.widget.Toast;

public class HttpGetData {
	private String retResponse =null;

	public String HttpGets(String url,String param ){		
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		HttpClient client = new DefaultHttpClient(params);
		url = url + param;
		HttpGet httpGet = new HttpGet(url);		
		try {
			HttpResponse httpResponse = client.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK)
			{							
				retResponse = EntityUtils.toString(httpResponse.getEntity());		
				
			}			
		} catch (ClientProtocolException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
			
		}	
		return retResponse;  
	}		
}
