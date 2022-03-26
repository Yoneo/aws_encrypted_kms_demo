import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

public class S3Sample_encrypted_kms {
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		//BEGIN configuration
		
		// specify the region
		Regions region = Regions.EU_CENTRAL_1;

		// Set AWS Credentials
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("XXXXXXXXXXXX",
				"XXXXXXXXXXXXX");

		// specify the bucket name
		String bucketName = "XXXXXXXXXXXXXXXXXX";
		
		// specify an AWS KMS key ID
		String keyId = "XXXXXXXXXXXXXXXXXXXXXX";
				
		// specify the location of the file that has to be loaded up
		String uploadFileLocation = "./data/MOCK_DATA.csv";
		
		// specify the location for the download of the file
		String downloadFileLocation = "./data/MOCK_DATA-Download.csv";
		
		// specify name of the Object
		String s3ObjectKey = "mockdata";
		
		//END configuration
		
		System.out.println("AWS S3 - Java sample\nProject: LucaNet_HTW_WiSe21_22");

		try {
			
			//start the sample code 
			S3Sample_encrypted_kms_helper helper = new S3Sample_encrypted_kms_helper();
			helper.samplemethods(region, awsCreds, bucketName, keyId, uploadFileLocation, downloadFileLocation, s3ObjectKey);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("\n" + "Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {

			System.out.println("Error Message: " + ace.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}