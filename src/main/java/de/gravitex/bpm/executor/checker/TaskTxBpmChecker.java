package de.gravitex.bpm.executor.checker;

import de.gravitex.bpm.executor.checker.base.BpmAsserter;
import de.gravitex.bpm.executor.checker.base.BpmStateChecker;

public class TaskTxBpmChecker extends BpmStateChecker {

	@Override
	protected BpmAsserter createBpmAsserter() {
		return BpmAsserter.instance();
	}
}