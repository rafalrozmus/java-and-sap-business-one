/*
 * A simple example on how to add new Purchase Request to SAP Business One.
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
import java.sql.Date;

/**
 *
 * @author rozmus.rafal@gmail.com
 */
public class AddPurchaseRequest {
    
    // declare Company object
    public ICompany company;
    // declare Document object
    public IDocuments purchaseRequest;
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    
    public static void main(String[] args) 
    {
        AddPurchaseRequest sapConnection = new AddPurchaseRequest();
        
        sapConnection.connect();
        
        if (sapConnection.connect()) {
            sapConnection.addPurchaseRequest();
            sapConnection.disconnect();
        }   
    }
    
    
    public void addPurchaseRequest()
    {
        try
        {
            // Initialise Documents object and set the document type to Purchase
            // Request. 1470000113 indicates Purchase Request. For a list of Object Types check
            // http://www.sapbusinessonesdk.co.uk/2015/12/17/sap-business-one-object-types/
            purchaseRequest = SBOCOMUtil.newDocuments(company, 1470000113);
            
            // Set Request Type. The valid values are: '12' - 'User',  '171' - 'Employee'
            purchaseRequest.setReqType(12);
            
            // Set Requester. For 'User' type enter username.
            // For 'Emnployee' type enter Employee Number (OHEM.empID field in the database)
            purchaseRequest.setRequester("rafalr");
            
            // Set Item/Service Type, 0=Items, 1=Service
            purchaseRequest.setDocType(0);
            
            // Set Document Dates
            purchaseRequest.setDocDate(Date.valueOf("2018-02-05"));
            purchaseRequest.setRequriedDate(Date.valueOf("2018-03-01"));
            
            // Add 1st line
            purchaseRequest.getLines().add();
            purchaseRequest.getLines().setCurrentLine(0);
            purchaseRequest.getLines().setItemCode("LUCY10");
            purchaseRequest.getLines().setQuantity(1.0);

            // Add 2nd line
            purchaseRequest.getLines().add();
            purchaseRequest.getLines().setCurrentLine(1);
            purchaseRequest.getLines().setItemCode("PEKI41");
            purchaseRequest.getLines().setQuantity(5.0);

            
            // add Purchase Request
            if (purchaseRequest.add() == 0) 
            {
                System.out.println("Successfully added Sales Order");
            }
            else 
            {
                // get error message fom SAP Business One Server
                SBOErrorMessage errMsg = company.getLastError();
                System.out.println(
                        "Cannot add Sales Order: "
                        + errMsg.getErrorMessage()
                        + " "
                        + errMsg.getErrorCode()
                );
            }
        }
        catch(SBOCOMException e)
        {
            e.printStackTrace();
        }
        
    }
    
    
    /**
     * Set all connection parameters, connect to SAP Business One and initialise
     * company instance.
     */
    public boolean connect() 
    {  
        try 
        {
            // initialise company instance
            company = SBOCOMUtil.newCompany();
            // set database server host
            company.setServer("dbserver");
            // set company database
            company.setCompanyDB("CompanyDbName");
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
            company.setDbPassword("DbPassword");
            // set license server and port
            company.setLicenseServer("licenseserver:30000");
            
            // initialise connection
            connectionResult = company.connect();
            
            // if connection successful
            if (connectionResult == 0) 
            {
                System.out.println("Successfully connected to " + company.getCompanyName());
                return true;
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
                return false;
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
}
