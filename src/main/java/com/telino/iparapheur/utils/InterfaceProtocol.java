package com.telino.iparapheur.utils;

public final class InterfaceProtocol {

	private InterfaceProtocol() {
		throw new AssertionError("Instantiation not allowed!");
	}

	public static enum ComObjectKey {
		RETURN_CODE("codeRetour"), WORKFLOW_INS_ID("workflowinstanceid"), ERROR_MESSAGE(
				"messageErreur"), WORKFLOW_STATUS("workflowStatut"), DOCUMENT_ID("documentID");

		private String value;

		private ComObjectKey(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static enum ReturnCode {
		OK("OK", "11"), KO("KO", "9"), NIL("", "");

		private String value;

		private String code;

		private ReturnCode(String value, String code) {
			this.value = value;
			this.code = code;
		}

		@Override
		public String toString() {
			return value;
		}

		public String getCode() {
			return code;
		}
	}

	public static enum ApplicationMetier {
		CIVIL, OXALIS, NEOGED;
	}

	public static enum WorkflowType {
		CIVIL(4), OXALYS(14), NEOGED_TEST(4);

		private int val;

		private WorkflowType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}
	}
	
	public static enum AppUserAccesIndex {
		PARAPHEUR(2), STAGIAIRE3(5);
		
		private int index;
		
		private AppUserAccesIndex(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}	
		
	}
}