package com.xpand.xface.util;

import java.io.File;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HWFileUploader{
    private OkHttpClient okHttpClient = null;
    public static final String FILE_IMAGE_JPG = "image/jpg";
    public void process(String uploadURL, String uploadId, String uploadFileName){
        try{            
            File file = new File(uploadFileName);            
            //this.upload(file, strURLUpload+"?action=upload&uploaded-file-id="+strUploadId, strFileName, "upload", strUploadId);
            String result = "result is null";
            this.getOKHttpConnection();    
            //File file, String uploadURL, String uploadFileName, String uploadFileSize, String action, String uploadId, String uploadFileType
            if (this.uploadFile(file, uploadURL, uploadFileName, "12345", "upload", uploadId, HWFileUploader.FILE_IMAGE_JPG)!=null){
//            	HttpServiceUtil oHttp = new HttpServiceUtil();
//            	result = oHttp.invokeRestfulService("xxx", "https://192.168.2.53:8009/sdk_service/rest/image-library/publish-uploaded-file/v1.1"
//            			, "<request><casefile><modify-timestamp>"+StringUtil.dat+"</modify-timestamp><name>"+strFileName+"</name><sdb-group-id>1</sdb-group-id><source-system-id>0</source-system-id><process><video-analysis><face>true</face></video-analysis></process></casefile><uploaded-file-id>"+strUploadId+"</uploaded-file-id></request>", "POST");//            	
            }            
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("---Exception---" + e.toString());
        }
        
    }
    
    public String uploadFile(File file, String uploadURL, String uploadFileName, String uploadFileSize, String action, String uploadId, String uploadFileType) throws Exception{
        
        //RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
    	RequestBody fileBody = RequestBody.create(MediaType.parse(uploadFileType), file);
        RequestBody requestBody =
            new MultipartBody.Builder().setType(MultipartBody.FORM)
            	.addFormDataPart("action", action)
            	.addFormDataPart("uploaded-file-id", uploadId)
            	.addFormDataPart("begin", "0")
            	.addFormDataPart("length", uploadFileSize)
                .addFormDataPart("imgInput", uploadFileName, fileBody)
                .build();        
        Request request = new Request.Builder().url(uploadURL).post(requestBody).build();        
        Response response;
        try{
            response = okHttpClient.newCall(request).execute();            
            if (response.isSuccessful()){                
                return response.toString();
            }else{
            	throw new Exception("upload error code " + response);
            }            
        }catch (Exception e){
            e.printStackTrace();
        }        
        return null;
    }
    private void getOKHttpConnection(){
    	OkHttpClient.Builder builder = new OkHttpClient.Builder();
    	try {
    		// Create a trust manager that does not validate certificate chains
    	    final TrustManager[] trustAllCerts = new TrustManager[] {
    	        new X509TrustManager() {
    	          @Override
    	          public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
    	          }

    	          @Override
    	          public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
    	          }

    	          @Override
    	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    	            return new java.security.cert.X509Certificate[]{};
    	          }
    	        }
    	    };

    	    // Install the all-trusting trust manager
    	    final SSLContext sslContext = SSLContext.getInstance("SSL");
    	    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
    	    // Create an ssl socket factory with our all-trusting manager
    	    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

    	    builder = new OkHttpClient.Builder();
    	    
    	    builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
    	    builder.hostnameVerifier(new HostnameVerifier() {
    	      @Override
    	      public boolean verify(String hostname, SSLSession session) {
    	        return true;
    	      }
    	    });
    	}catch (Exception ex){
    		
    	}
    	this.okHttpClient = builder.build();
    }    
}
