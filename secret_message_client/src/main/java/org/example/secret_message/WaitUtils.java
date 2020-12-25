package org.example.secret_message;

import java.util.function.BooleanSupplier;

/**
 * @author weicheng.zhao
 * @date 2020/12/25
 */
public final class WaitUtils {

    public static void wait(BooleanSupplier supplier, long mills) {
        long start = System.currentTimeMillis();
        while (true) {
	        long cost = System.currentTimeMillis() - start;
	        if(supplier.getAsBoolean() || cost > mills){
        		break;
	        }
        }
    }
}
