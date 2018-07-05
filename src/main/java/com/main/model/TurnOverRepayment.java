package com.main.model;

/**
 * Created by
 * jiewai on 2018/7/12.
 */
public class TurnOverRepayment implements Comparable<TurnOverRepayment>{

    /**
     * 移交日期
     */
    private String transfer;
    /**
     * 移交还款金额
     */
    private Double handOverAmount;
    /**
     * 实际还款金额
     */
    private Double curAmount;
    /**
     * 移交金额
     */
    private Double amount;
    /**
     * T+1 天还款
     */
    private Double[] daySums;
    /**
     * T+1 天还款率
     */
    private Double[] xArray;
    /**
     * 当前借据当前还款(没有累加的值)
     */
    private Double[] thisDaySum;
    /**
     * 最大天数
     */
    private int maxDays;

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public Double getHandOverAmount() {
        return handOverAmount;
    }

    public void setHandOverAmount(Double handOverAmount) {
        this.handOverAmount = handOverAmount;
    }

    public Double getCurAmount() {
        return curAmount;
    }

    public void setCurAmount(Double curAmount) {
        this.curAmount = curAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double[] getDaySums() {
        return daySums;
    }

    public void setDaySums(Double[] daySums) {
        this.daySums = daySums;
    }

    public Double[] getxArray() {
        return xArray;
    }

    public void setxArray(Double[] xArray) {
        this.xArray = xArray;
    }

    public Double[] getThisDaySum() {
        return thisDaySum;
    }

    public void setThisDaySum(Double[] thisDaySum) {
        this.thisDaySum = thisDaySum;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(TurnOverRepayment o) {
        return this.transfer.compareTo(o.transfer);
    }
}
