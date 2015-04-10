/*
 * A simple example on how to use Recordset object to retrieve data from 
 * SAP Business One server.
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
 * @author Rafal.Rozmus
 */
public class RecordsetExample {
    
    // declare Company object
    public ICompany company;
    // declare Recordset object
    public IRecordset recordSet;
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    
    public static void main(String[] args) 
    {
        RecordsetExample sapConnection = new RecordsetExample();
        sapConnection.connect();
        sapConnection.getBPList();
        // check if connection has been established before disconnecting
        if (sapConnection.getConnectionResult() == 0)
        {
            sapConnection.disconnect();
        }    
    }
    
    /**
     * Use Recordset object to print a list of all Business Partner codes and 
     * names from SAP Business One by executing SQL query. 
     */
    public void getBPList()
    {
        try
        {
            // initialise Recordset object
            recordSet = SBOCOMUtil.newRecordset(company);
            int index;
            // perform SQL query
            recordSet.doQuery("SELECT CardCode, CardName FROM OCRD ORDER BY CardCode");
            index = recordSet.getRecordCount();
            // print out column names first - CardCode(item 0) and CardName(item 1)
            System.out.println(recordSet.getFields().item(0).getName()
                + "\t\t"
                + recordSet.getFields().item(1).getName()
            );
            
            // move to the first row
            recordSet.moveFirst();
            // iterate through all rows
            for (int i = 0; i < index; i++)
            {
                System.out.println(recordSet.getFields().item(0).getValue()
                    + "\t\t"
                    + recordSet.getFields().item(1).getValue()
                );
                // move to next row
                recordSet.moveNext();
            }             
            recordSet.release();
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
