/*
 * A simple example on how to mass update Business Partner Catalogue numbers.
 * For this program to work, you need SAP Business One DI API installed.
 */

/*
 * Import JCO libraries from DI API folder. Two files need to be added to your
 * project libraries - sboapi.jar and sbowrapper.jar
 * The files are usually located in:
 * C:\Program Files (x86)\SAP\SAP Business One DI API\JCO\LIB - 32 bit build
 * C:\Program Files\SAP\SAP Business One DI API\JCO\LIB - 64 bit build
 */

import com.sap.smb.sbo.api.*;
/**
 *
 * @author Rafal.Rozmus (rozmus.rafal@gmail.com)
 */
public class UpdateAlternateCatNumbersExample {
    
    // declare Company object
    public ICompany company;
    // declare Business Partner Catalogue Numbers object
    public IAlternateCatNum altCatNum;
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    // declare a list of Business Partners
    private String[] bpList = {"AAAA"};
    // declare a list of product codes
    private String[] productsList = {"ITEM001" ,"ITEM002", "ITEM003", "ITEM004"};
    // declare a list of current BP Catalgue numbers
    private String[] currentCatNumbersList = {"23922221", "23922222", "23922223", "23922224"};
    // declare a list of new BP Catalogue Numbers
    private String[] newCatNumbersList = {"23922266", "23922267", "23922268", "23922269"};
    
    public static void main(String[] args) {
        UpdateAlternateCatNumbersExample sapConnection = new UpdateAlternateCatNumbersExample();
        sapConnection.connect();
        sapConnection.updateCategoryNumbers();
        // check if connection has been established before disconnecting
        if (sapConnection.getConnectionResult() == 0)
        {
            sapConnection.disconnect();
        }
    }
    
    /**
     * Set all connection parameters, connect to SAP Business One and initialise
     * company instance.
     */
    public void connect() 
    {  
        try 
        {
            // initialise company instance
            company = SBOCOMUtil.newCompany();
            // set database server host
            company.setServer("dbserver");
            // set company database
            company.setCompanyDB("SBODemoGB");
            // set SAP user
            company.setUserName("manager");
            // set SAP user password
            company.setPassword("Password");
            // set SQL server version
            company.setDbServerType(SBOCOMConstants.BoDataServerTypes_dst_MSSQL2012);
            // set whether to use trusted connection to SQL server
            company.setUseTrusted(false);
            // set SAP Business One language
            company.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
            // set database user
            company.setDbUserName("sa");
            // set database user password
            company.setDbPassword("SQLPassword");
            // set license server and port
            company.setLicenseServer("licenserver:30000");
            
            // initialise connection
            connectionResult = company.connect();
            
            // if connection successful
            if (connectionResult == 0) 
            {
                System.out.println("Successfully connected to " + company.getCompanyName());
            }
            // if connection failed
            else 
            {
                // get error message fom SAP Business One Server
                SBOErrorMessage errMsg = company.getLastError();
                System.out.println(
                        "Cannot connect to server: "
                        + errMsg.getErrorMessage()
                        + " "
                        + errMsg.getErrorCode()
                );
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            connectionResult = -1;    
        }
    }
    
    /**
     * Disconnect from SAP Business One server.
     */
    public void disconnect() 
    {
        company.disconnect();
        System.out.println("Application disconnected successfully");
    }
    
    /**
     * Get connection result
     * 
     * @return 0 if success, -1 if fail
     */
    public int getConnectionResult()
    {
        return connectionResult;
    }
    
    public void updateCategoryNumbers() {
        try {
            altCatNum = SBOCOMUtil.newAlternateCatNum(company);
            
            // iterate through all Business Partners
            for (int i = 0; i < bpList.length; i++) {
                // iterate through all products
                for (int y = 0; y < productsList.length; y++) {
                    altCatNum.getByKey(productsList[y], bpList[i], currentCatNumbersList[y]);
                    System.out.println("BP #" + i + " - BP: " + altCatNum.getCardCode() 
                        + ", Product: " + altCatNum.getItemCode()
                        + ", BP Category Number: " + altCatNum.getSubstitute()
                        + ", New Category Number: " + newCatNumbersList[y]
                    );
                    System.out.println("Updating ...");
                    altCatNum.setSubstitute(newCatNumbersList[y]);
                    if (altCatNum.update() == 0) {
                        System.out.println("Success!");
                    } else {
                        // get error message fom SAP Business One Server
                        SBOErrorMessage errMsg = company.getLastError();
                        System.out.println(
                            "Failed to update: "
                            + errMsg.getErrorMessage()
                            + " "
                            + errMsg.getErrorCode()
                        );
                    }
                    
                }
            }
        }
        catch(SBOCOMException e)
        {
            e.printStackTrace();
        }
    }
            
}
