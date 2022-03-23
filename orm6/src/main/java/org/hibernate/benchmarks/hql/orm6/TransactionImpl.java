package org.hibernate.benchmarks.hql.orm6;

import org.hibernate.Transaction;
import org.hibernate.benchmarks.hql.PersistenceContext;

/**
 * @author Steve Ebersole
 */
public class TransactionImpl implements PersistenceContext.Transaction {
	private final PersistenceContextImpl pc;
	private Transaction hibTxn;

	public TransactionImpl(PersistenceContextImpl pc) {
		this.pc = pc;
	}

	@Override
	public boolean isActive() {
		return hibTxn != null && hibTxn.isActive();
	}

	@Override
	public void begin() {
		if ( hibTxn != null ) {
			// no-op
			return;
		}

		hibTxn = pc.getSession().beginTransaction();
	}

	@Override
	public boolean commit() {
		if ( hibTxn == null || !hibTxn.isActive() ) {
			return false;
		}

		try {
			hibTxn.commit();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean rollback() {
		if ( hibTxn == null || !hibTxn.isActive() ) {
			return false;
		}

		try {
			hibTxn.rollback();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
}
