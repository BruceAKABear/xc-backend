package net.zacard.xc.common.biz.infra.exception;

/**
 * @author guoqw
 * @since 2020-05-22 21:39
 */
public class UncheckedException extends RuntimeException {
    private static final long serialVersionUID = 4140223302171577501L;

    public UncheckedException(Throwable wrapped) {
        super(wrapped);
    }

    @Override
    public String getMessage() {
        return super.getCause().getMessage();
    }
}
