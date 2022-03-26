import java.io.File;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3EncryptionClientV2Builder;
import com.amazonaws.services.s3.AmazonS3EncryptionV2;
import com.amazonaws.services.s3.model.CryptoConfigurationV2;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;

public class S3Sample_encrypted_kms_helper {

	public void samplemethods(Regions region, com.amazonaws.auth.BasicAWSCredentials awsCreds,
			java.lang.String bucketName, java.lang.String keyId, String uploadFileLocation, String downloadFileLocation,
			String s3ObjectKey) {

		//convert to other format so that it can be used in the AmazonS3EncryptionV2 method
		String region2 = region.getName();

		System.out.println("\n" + "AWS KMS Key ID.....  " + keyId);
		System.out.println("The default region: " + region);

		System.out.println("\n" + "Starting Encryption Initialization ");
		
		// create crypto object
		AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withCryptoConfiguration(
						new CryptoConfigurationV2().withCryptoMode((CryptoMode.StrictAuthenticatedEncryption))
								.withAwsKmsRegion(RegionUtils.getRegion(region2)))
				.withEncryptionMaterialsProvider(new KMSEncryptionMaterialsProvider(keyId)).build();
		System.out.println("\n" + "Encryption Initialization Complete.... ");

		// File upload
		s3Encryption.putObject(bucketName, s3ObjectKey, new File(uploadFileLocation));

		// Displays File content to demonstrate that decryption works
		System.out.println(s3Encryption.getObjectAsString(bucketName, s3ObjectKey));

		// This is where the downloaded file will be saved
		File localFile = new File(downloadFileLocation);

		// Downloads the file
		s3Encryption.getObject(new GetObjectRequest(bucketName, s3ObjectKey), localFile);

		// Check, if download was successful
		boolean success = localFile.exists() && localFile.canRead();
		System.out.println("File saved: " + success);

		// Close Encryption Session
		s3Encryption.shutdown();
		System.out.println("Encryption Session closed");

	}
}
