package vn.ncsc.visafe.dns.net.setting

class RandomString {
    // function to generate a random string of length n
    fun getAlphaNumericString(n: Int): String {

        // chose a Character random from this String
        val alphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz")

        // create StringBuffer size of AlphaNumericString
        val sb = StringBuilder(n)
        for (i in 0 until n) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            val index = (alphaNumericString.length
                    * Math.random()).toInt()

            // add Character one by one in end of sb
            sb.append(alphaNumericString[index])
        }
        return sb.toString()
    }
}