/*
 * A simple example on how to mark several AR Invoices as printed. 
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
 * @author Rafal Rozmus <rozmus.rafal@gmail.com>
 */
public class MarkInvoiceAsPrinted {
    
    // declare Company object
    public ICompany company;
    // declare Document object
    public IDocuments invoice;
    // declare Recordset object
    public IRecordset recordSet;
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    
    public static void main(String[] args) 
    {
        MarkInvoiceAsPrinted sapConnection = new MarkInvoiceAsPrinted();
        sapConnection.connect();
        sapConnection.markInvoices();
        // check if connection has been established before disconnecting
        if (sapConnection.getConnectionResult() == 0)
        {
            sapConnection.disconnect();
        }    
    }
    
    /**
     * Mark all today's AR Invoices as printed
     */
    public void markInvoices() {
        try
        {
            // Initialise Recordset object
            recordSet = SBOCOMUtil.newRecordset(company);
            // Initialise Documents object and set the type to AR Invoice
            // 13 means AR Invoice. For a list of Object Types check
            // http://www.sapbusinessonesdk.co.uk/2015/12/17/sap-business-one-object-types/
            invoice = SBOCOMUtil.newDocuments(company, 13);
            // Perform SQL query. Select all today's Invoices that haven't been printed
            recordSet.doQuery("SELECT T0.[DocEntry] FROM OINV T0 WHERE T0.DocDate = CAST(CURRENT_TIMESTAMP AS DATE) AND T0.[Printed] = 'N'");
            // Move to the first record
            recordSet.moveFirst();
            // Go through all records
            while (!recordSet.isEoF()) {
                // Retrieve AR Invoice by it's OINV.DocEntry value
                invoice.getByKey(recordSet.getFields().item(0).getValueInteger());
                invoice.setPrinted(1);
                invoice.update();
                recordSet.moveNext();
            }
            
        }
        catch (SBOCOMException e) 
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
