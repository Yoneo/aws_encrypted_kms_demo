# Blueprint AWS (full guide)

This document shows exemplarily how the cloud storage Amazon Simple Storage Service (S3) can be connected using Java.

| Detail | Value |
| ------------------------------------ | ------------------------------------------------------------ |
| Eclipse version used | Eclipse IDE for Java Developers 2021-12 (4.22.0) |
| JRE | JavaSE-17 |
| AWS Java SDK | Version 2 |
| Created in | Beginning 2022 |
| License | CC0 (except the contents of the screenshots) |

## 1.Setup Cloud Environment

First, an AWS account must be created and verified. This requires the [deposit of a payment method](https://aws.amazon.com/premiumsupport/knowledge-center/free-tier-payment-method/https:/aws.amazon.com/premiumsupport/knowledge-center/free-tier-payment-method/) , even if the free offer [AWS Free Tier](https://aws.amazon.com/free/) is to be used. There are no special features to consider when creating an account, so reference is made to the [official instructions](https://aws.amazon.com/de/premiumsupport/knowledge-center/create-and-activate-aws-account/) .

### 1.1 Account

[Screencast](https://youtu.be/ZRLSC08UEQ0) (german, approx. 3 min)

There are several options for authentication. The account id (access key ID) and the key (secret access key) can be stored as variables in the code or an external file. Furthermore, it is possible to store the keys as an environment variable on the executing system (see [AWS documentation for instructions](https://docs.aws.amazon.com/en_us/sdk-for-java/latest/developer-guide/setup.html#setup-credentials)). With this variant, it is also possible to provide several keys (e.g. for different accounts) (see [AWS documentation for instructions](https://docs.aws.amazon.com/en_us/sdk-for-java/latest/developer-guide/setup-additional.html#setup-additional-credentials)). In addition, the key can be stored in the AWS Eclipse plugin. For more information about authentication, see the official [AWS documentation](https://docs.aws.amazon.com/en_us/sdk-for-java/latest/developer-guide/credentials.html). In this example, we use the variant with local variables.

Dedicated authorizations can be realized on AWS via so-called IAM users (Identity and Access Management). Before creating the IAM account, the bucket and KMS key must have been set up (see the following chapter).

The IAM user is created using the AWS Management Console. The AWS credential type _Access key_ must be selected.

IMAGE![](documentation_screenshots\Picture1.png)

The policy template (json file `IAM_policy.txt`) can be used for the IAM authorizations. The identifier of the bucket and the KMS key must be added. 

The permissions correspond exactly to the requirements of the following demo code, but no object permissions are assigned. This allows the IAM user to access and decrypt all *objects* in the bucket if the defined KMS key has been used. In addition, new objects can be encrypted and uploaded.![]()

| Right | Function |
| ------------------- | ------------------------------------------------------------ |
| s3:PutObject | Storage of the *object* in the bucket |
| s3:GetObject | Retrieve the *object* from the bucket |
| kms:Decrypt | Decryption of the *object* |
| kms:GenerateDataKey | Retrieval of the data key for local encryption of the *object* |

To configure the permissions , the aforementioned JSON code is inserted via _Create policy_. The *ARN identifier (Amazon Resource Name*) of the *S3 bucket* and the *KMS key* must be entered in it. Both data can be found in the management console in the respective areas.
This policy is named and saved. It can then be added to the IAM user:

![](documentation_screenshots\Picture2.png)

![](documentation_screenshots\Picture3.png)

### 1.2 Storage

[Screencast](https://youtu.be/k8KSJmf1LH4) (german, approx. 4 min)

A *bucket* is used to group *objects*. Objects, in turn, consist of data and its description (metadata). For example, an object is a file. A *bucket* can contain any number of objects.

To use the sample code, a bucket must first be created. This is done through the S3 section of the *AWS Management Console*.

The name of the *bucket* can only be assigned once across AWS. Therefore, it is recommended to work with long, unique names.

An AWS data center close to future users should be chosen as the region. It should be noted that different prices apply to the regions.

For the test, ACL *(access control lists)* and *public access* remain disabled. Depending on the intended use, bucket versioning can be switched on. Storage of intermediate versions may lead to corresponding costs.

*Server-side encryption* is enabled. The _Encryption key type_ is the SSE-KMS (*Key Management Service key*).

A new KMS key can then be created:

![](/documentation_screenshots\Picture4.png)

### 1.3 Key Management System

The AWS Key Management System (KMS) is used to manage keys in the Amazon AWS infrastructure. Extensive administration and control functions are offered. You can also upload your own keys. Further information can be found on the [KMS website.](https://aws.amazon.com/en/kms/)

#### 1.3.1 Create KMS Key

Symmetric encryption is selected as the *key type:*

![](documentation_screenshots\Picture5.png)

The name is only used for assignment and can also be changed later.

For the key administrators and key usage permissions, an IAM user must be selected (see Chapter 3.1). 

#### 1.3.2 Link bucket to KMS Key

After that, the *KMS key* can ![](/documentation_screenshots\6.png) be selected (if necessary after clicking the reload icon) when creating the *bucket.*

A *bucket key* is used to reduce costs because a separate KMS key is not created for each object. Instead, a bucket-wide, unified key is used. This also results in fewer requests (see [AWS documentation](https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucket-key.html) for details).

After the bucket has been created, the bucket name and *KMS Key ID* are stored in the sample code:

![](documentation_screenshots\7.png)

The ID of the *KMS key* can be found in the AWS Management Console when the corresponding key is selected under KMS.

## 2. Development

There are no specific settings to be made when installing Eclipse (official [download and installation instructions](https://www.eclipse.org/downloads/packages/installer)). An installation of the AWS Eclipse plugin is not required for the sample code.

In order for the encryption to be used, the Bouncy Castle *JAR* must be integrated into *Eclipse Build Path.*

[Screencast](https://youtu.be/1ZPeXD3a010) (german, 30 sec)

The file can be downloaded from the [website bouncycastle.org.](https://bouncycastle.org/latest_releases.html) After that, a right-click on the Eclipse project is made and under *Build Path* the function *Add External Archives.*.. elected:

![](documentation_screenshots\Picture8.png)

Select the downloaded JAR file.

### 2.1 Dependencies

For Maven, the package dependencies are described in the `.pom` file. To use AWS, the *AWS Java SDK* must be included. This contains all required objects and methods for the demo application. To use Java in version 1.6 or higher, the [JAXB API](https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api/) for XML processing is also required. For more information on using Maven with AWS, see the official [documentation.](https://docs.aws.amazon.com/en_us/sdk-for-java/latest/developer-guide/setup-project-maven.html)

The [Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/) in the build area is used exclusively for export as a JAR file. The extension includes all dependencies in the JAR file, so that this file runs independently.

### 2.2 Starter Class

The Starter class `S3Sample_encrypted_kms.java` contains the configuration variables, creates the Helper class object, and executes the upload and download method. 

The creation of the *AWS credentials, bucket*, and *key ID* were shown in Chapter 3. The path of the file to be uploaded (`uploadFileLocation`), the download (`downloadFileLocation`) and the object key (`s3ObjectKey`; uniquely designated object to be uploaded and downloaded) can be selected according to the folder and file structure.

### 2.3 Helper Class

In class `S3Sample_encrypted_kms.java`, the necessary objects are created and the methods for uploading and downloading are called. Explanations of the individual methods can be found as comments in the sample code.

#### 2.3.1 Client-side encryption

With client-side encryption, you can choose two options: a key stored in AWS KMS and a locally managed key. AWS KMS is a service for creating and controlling cryptographic keys. The keys thus lie in Amazon's infrastructure. For more information, see the official [AWS documentation](https://docs.aws.amazon.com/en_us/kms/latest/developerguide/overview.html). When deciding on one of the two variants, a main criterion is which infrastructure (your own or that of Amazon) is most trusted. 

In the example code, a *KMS* customer *master key (CMK)* is used (see Chapter 3.3.1). This is permanently in amazon's infrastructure and is only addressed via API. So we use only a *Data Key,* which is responsible for the encryption of the data and other data keys. *Data keys* can occur in encrypted or unencrypted form (see [AWS documentation](https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#data-keys)). The unencrypted version of the data key (plaintext) is required for encryption.

#### 2.3.2 Upload


    AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard(). withRegion(region)
    . withCredentials(new AWSStaticCredentialsProvider(awsCreds))
    . withCryptoConfiguration( new CryptoConfigurationV2().withCryptoMode((CryptoMode.StrictAuthenticatedEncryption))
    . withAwsKmsRegion(RegionUtils.getRegion(region2))
    . withEncryptionMaterialsProvider(new KMSEncryptionMaterialsProvider(keyId)).build();

Initialization takes place by creating the `AmazonS3EncryptionV2` object. The AWS Region (`region`) defined in the starter class and the access data (`awsCreds`) are passed.

Then the configuration of the encryption is made (`CryptoConfigurationV2`). The Crypto Mode *StrictAuthenticatedEncryption* requires authenticated encryption with AES. If this cannot be realized, the operation is aborted (*output of a security exception*). The bucket and KMS regions must be identical, so only one variable is set in the Starter class and not distinguished. Because the `withAwsKmsRegion` method requires a different form of region specification, the class is converted to the `region2` variable at the beginning of the class. The KMS Key ID is set via the `KMSEncryptionMaterialsProvider`.

```
s3Encryption. putObject(bucketName, s3ObjectKey, new File(uploadFileLocation));
```

Using the above method, the test file is encrypted and uploaded with the *data key*. The name of the bucket (`bucketName`), the object label (`s3ObjectKey`) and the location of the file (`uploadFileLocation`) from the Starter class are used.

#### 2.3.3 Download

```
System. out. println(s3Encryption. getObjectAsString(bucketName, s3ObjectKey));
```

For testing purposes, the previously created object is retrieved as a text (string) using the `getObjectAsString` method. The output takes place on the console. This method shows that the decryption is working correctly.

```
File localFile = new File(downloadFileLocation);
s3Encryption.getObject(new GetObjectRequest(bucketName, s3ObjectKey), localFile);
```

To download the file, an object of type File is created, which contains the path to the desired storage location of the object (`downloadFileLocation`) from the Starter class. The `s3Encryption.getObject` method is used to download and decrypt the file. The name of the bucket (`bucketName`) and the object label (`s3ObjectKey`) from the Starter class are used.

```
boolean success = localFile.exists() && localFile.canRead();
System.out.println("File saved: " + success);
```

For test wakes, a Boolean is determined, which is true if the file exists and is readable. This Boolean is output to the console.

The following method `s3Encryption.shutdown` ends the session.

## 3. Futher information

●Code Examples: https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2
●API Documentation: https://sdk.amazonaws.com/java/api/latest/
●Official GitHub Repo: https://github.com/aws/aws-sdk-java-v2
●Maven Repo: https://mvnrepository.com/artifact/software.amazon.awssdk/s3