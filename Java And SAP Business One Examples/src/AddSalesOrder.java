/*
 * A simple example on how to add new Sales Order to SAP Business One.
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
public class AddSalesOrder {
    
    // declare Company object
    public ICompany company;
    // declare Document object
    public IDocuments salesOrder;
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    
    public static void main(String[] args) 
    {
        AddSalesOrder sapConnection = new AddSalesOrder();
        
        sapConnection.connect();
        sapConnection.addSalesOrder();
        // check if connection has been established before disconnecting
        if (sapConnection.getConnectionResult() == 0)
        {
            sapConnection.disconnect();
        }    
    }
    
    
    public void addSalesOrder()
    {
        try
        {
            // initialise Documents object and set the document type to Sales
            // Order. 17 means Sales Order. For a list of Object Types check
            // http://scn.sap.com/community/business-one/blog/2013/07/09/sap-business-one-form-types-and-object-types
            salesOrder = SBOCOMUtil.newDocuments(company, 17);
            // Set Business Partner
            salesOrder.setCardCode("ZZZ002");
            // Set Item/Service Type, 0=Items, 1=Service
            salesOrder.setDocType(0);
            // Set Posting Date
            salesOrder.setDocDate(Date.valueOf("2015-06-10"));
            // Set Delivery Date
            salesOrder.setDocDueDate(Date.valueOf("2015-09-25"));
            
            // Add 1st line
            salesOrder.getLines().add();
            salesOrder.getLines().setCurrentLine(0);
            salesOrder.getLines().setItemCode("ITEM001");
            salesOrder.getLines().setQuantity(1.0);

            // Add 2nd line
            salesOrder.getLines().add();
            salesOrder.getLines().setCurrentLine(1);
            salesOrder.getLines().setItemCode("ITEM002");
            salesOrder.getLines().setQuantity(5.0);

            
            // add Sales Order
            if (salesOrder.add() == 0) 
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
}
