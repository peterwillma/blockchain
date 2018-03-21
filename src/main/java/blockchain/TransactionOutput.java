package blockchain;

import java.security.PublicKey;

public class TransactionOutput {

	public String id;
	public PublicKey reciepient;
	public float value;
	public String parentTransactionsId;

	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionsId = parentTransactionId;
		this.id = StringUtil
				.applySha256(StringUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
	}
	
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
}
