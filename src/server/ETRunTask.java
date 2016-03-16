package server;

import java.io.IOException;
import java.sql.SQLException;
import objectTransferrable.*;


public class ETRunTask implements ExecutableTask{

	private Server masterServer;
	private ClientInfo clientInfo;
	private ObjectTransferrable query;

	public ETRunTask(Server masterServer,
			ClientInfo clientInfo,
			ObjectTransferrable query) {
		this.masterServer = masterServer;
		this.clientInfo = clientInfo;
		this.query = query;
	}

	public Server getMasterServer() {
		return masterServer;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public ObjectTransferrable getQuery() {
		return query;
	}

	@Override
	public void run() {
		//create a new QueryManager object to handle the execution of the received object
		QueryManager runQuery = new QueryManager(getQuery(), getMasterServer(), this.clientInfo);

		//execute the query, updating the object
		try {
			runQuery.runOperation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//get the updated object and pass it back to the client
		try {
			if(!runQuery.getOperation().getOpCode().equals("0014")){
				getMasterServer().getServerModel().addToText("Sending back Object with opCode " + runQuery.getOperation().getOpCode() + "\n");
			}
			getClientInfo().getClientOutput().writeObject(runQuery.getOperation());
			if(!runQuery.getOperation().getOpCode().equals("0014")){
				getMasterServer().getServerModel().addToText("SENT" + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	

		//create a new instance of ETSearchForObject so to pick up more queries from this client and pass it to the threadpool
		ETSearchForObject newSearch = new ETSearchForObject(getMasterServer(), getClientInfo());
		getMasterServer().getThreadPool().execute(newSearch);
	}

}
