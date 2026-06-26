package com.example.data

object Config {
    // Official SMM Provider API credentials for MCNSMM
    const val API_URL = "https://morethanpanel.com/"
    const val API_KEY = "a31505e33313ff7687ff97103ff40fd3"
    
    // Fallback/App-specific configs matching the 7 main features
    const val DEFAULT_GLOBAL_MARGIN = 10.0 // Profit Margin System
    const val DEFAULT_UPI_ID = "mcnsmm@upi" // Add Funds UTR
    const val CHILD_PANEL_PRICE = 399.0 // Child Panel Rental Price (INR / BDT)
    const val APP_SUPPORT_WHATSAPP = "+91 98765 43210"
}
