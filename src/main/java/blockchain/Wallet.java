package blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public Wallet() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the KeyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// returns balance and stores the UTXOs owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: Noobchain.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) {
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		
		return total;
	}
	
	// Generates and returns a new transaction from this wallet
	public Transaction sendFunds(PublicKey _reciepient, float value) {
		if (getBalance() < value) {
			System.out.println("#Not Enough funds to send transaction. Transaction discarded.");
			return null;
		}
		
		// create array list of inputs
		ArrayList<TransactionInput> inputs = new ArrayList<>();
		
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if (total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, _reciepient, value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for (TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
	

}
