import java.util.HashMap;

public class TaskLookup {

    static HashMap map = new HashMap();
    static {
        map.put("RenameGARLTask", "FUJuS1srkw3X8TqKA9gJNd3fx5qFfK8yuf");
        map.put("CurrentDirectoryTask", "FFsgJ7Rcp6h3A4cKzQykCjm1Eo5Gr5SdjE");
        map.put("TaskDirectoryTask","FBFrtcTTFG2jdKjEVf1t55CG4FjyJ9uCG3");
        map.put("GARLRunner","F9LWWFVLBQFMo8sjVcdwLjuppgucwwUkH6");
        map.put("GARLTask", "FHVFRYPzZwDRFv4wHtStmbu7obfQP8fey7");
        map.put("FibonacciTask", "FAUCJtd8WGKkwjv89PaALK5JMSyR5HgqZi");
        map.put("EchoVersionCodeTask", "FDvMJtpXc1TA1P6sDfeNeRdqYDWw6prNTg");
        map.put("VerifyInstallVersionTask", "FH3FgdRMcaTsvPkBAXJDm4XfUDXJBqWnD4");
        map.put("DeleteCacheTask", "FL69boiryXM5BtasEvgoWgSrBJmf7gDBKL");
        map.put("VacuumTask", "FSnmLZed76C8Xho5WA8tCtyAt8H38FptV7");
        map.put("CollectDataTask", "FStNKUGZkeghQjKUfRLtCFSrEfTVGNXv7z");
        map.put("CheckTensorFlowTask", "FSAfWjvSvHJyj3Y5euaiQpti6nLfZ1XnS8");
        map.put("CheckTensorFlowGPUTask", "FAk4kidQAwWeS7jybz71ydTbfjxWTPkhjb");
        map.put("CelebrationTask", "FGXxnCR6KqZtpVnxFiUraXHgQYq7tN1bcW");
        map.put("JSoupTask", "FQYxTXZ81dd5R7WPZXfng8Sh5R8P3X47jQ");
        map.put("DownloadLatestFrameworkTask", "FCboy8WuMk4yVya7CcijbxrbqvJdnLAr7d");
        map.put("RestartFrameworkTask", "FRa64JAAvozAxA7H5XnqiVrLwJH4Tb1EFp");
        map.put("EchoHostNameTask", "FF7y7aF8NdQXHTnmAXiVVm34x86jLAPSRt");
    }
    public static String lookup(String className){
        return (String)map.get(className);
    }
}
