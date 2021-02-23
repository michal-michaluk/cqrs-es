package devices.configuration.data;

public enum PowerType {
    AC_1_PHASE, AC_3_PHASE, DC;

    static PowerType from(boolean isAc, Integer phases) {
        if (!isAc) {
            return DC;
        } else if (phases == 1) {
            return AC_1_PHASE;
        } else if (phases == 3) {
            return AC_3_PHASE;
        } else {
            throw new IllegalStateException("isAC " + isAc + " phases " + phases);
        }
    }
}
