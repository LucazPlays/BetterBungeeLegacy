package net.md_5.bungee.chat;

import java.util.*;
import com.google.common.base.*;
import com.google.gson.*;
import java.io.*;

public final class TranslationRegistry
{
    public static final TranslationRegistry INSTANCE;
    private final List<TranslationProvider> providers;
    
    private void addProvider(final TranslationProvider provider) {
        this.providers.add(provider);
    }
    
    public String translate(final String s) {
        for (final TranslationProvider provider : this.providers) {
            final String translation = provider.translate(s);
            if (translation != null) {
                return translation;
            }
        }
        return s;
    }
    
    public List<TranslationProvider> getProviders() {
        return this.providers;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TranslationRegistry)) {
            return false;
        }
        final TranslationRegistry other = (TranslationRegistry)o;
        final Object this$providers = this.getProviders();
        final Object other$providers = other.getProviders();
        if (this$providers == null) {
            if (other$providers == null) {
                return true;
            }
        }
        else if (this$providers.equals(other$providers)) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $providers = this.getProviders();
        result = result * 59 + (($providers == null) ? 43 : $providers.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return "TranslationRegistry(providers=" + this.getProviders() + ")";
    }
    
    private TranslationRegistry() {
        this.providers = new LinkedList<TranslationProvider>();
    }
    
    static {
        INSTANCE = new TranslationRegistry();
        try {
            TranslationRegistry.INSTANCE.addProvider(new JsonProvider("/assets/minecraft/lang/en_us.json"));
        }
        catch (Exception ex) {}
        try {
            TranslationRegistry.INSTANCE.addProvider(new ResourceBundleProvider("mojang-translations/en_US"));
        }
        catch (Exception ex2) {}
    }
    
    private static class ResourceBundleProvider implements TranslationProvider
    {
        private final ResourceBundle bundle;
        
        public ResourceBundleProvider(final String bundlePath) {
            this.bundle = ResourceBundle.getBundle(bundlePath);
        }
        
        @Override
        public String translate(final String s) {
            return this.bundle.containsKey(s) ? this.bundle.getString(s) : null;
        }
        
        public ResourceBundle getBundle() {
            return this.bundle;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ResourceBundleProvider)) {
                return false;
            }
            final ResourceBundleProvider other = (ResourceBundleProvider)o;
            if (!other.canEqual(this)) {
                return false;
            }
            final Object this$bundle = this.getBundle();
            final Object other$bundle = other.getBundle();
            if (this$bundle == null) {
                if (other$bundle == null) {
                    return true;
                }
            }
            else if (this$bundle.equals(other$bundle)) {
                return true;
            }
            return false;
        }
        
        protected boolean canEqual(final Object other) {
            return other instanceof ResourceBundleProvider;
        }
        
        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $bundle = this.getBundle();
            result = result * 59 + (($bundle == null) ? 43 : $bundle.hashCode());
            return result;
        }
        
        @Override
        public String toString() {
            return "TranslationRegistry.ResourceBundleProvider(bundle=" + this.getBundle() + ")";
        }
    }
    
    private static class JsonProvider implements TranslationProvider
    {
        private final Map<String, String> translations;
        
        public JsonProvider(final String resourcePath) throws IOException {
            this.translations = new HashMap<String, String>();
            try (final InputStreamReader rd = new InputStreamReader(JsonProvider.class.getResourceAsStream(resourcePath), Charsets.UTF_8)) {
                final JsonObject obj = new Gson().fromJson(rd, JsonObject.class);
                for (final Map.Entry<String, JsonElement> entries : obj.entrySet()) {
                    this.translations.put(entries.getKey(), entries.getValue().getAsString());
                }
            }
        }
        
        @Override
        public String translate(final String s) {
            return this.translations.get(s);
        }
        
        public Map<String, String> getTranslations() {
            return this.translations;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof JsonProvider)) {
                return false;
            }
            final JsonProvider other = (JsonProvider)o;
            if (!other.canEqual(this)) {
                return false;
            }
            final Object this$translations = this.getTranslations();
            final Object other$translations = other.getTranslations();
            if (this$translations == null) {
                if (other$translations == null) {
                    return true;
                }
            }
            else if (this$translations.equals(other$translations)) {
                return true;
            }
            return false;
        }
        
        protected boolean canEqual(final Object other) {
            return other instanceof JsonProvider;
        }
        
        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $translations = this.getTranslations();
            result = result * 59 + (($translations == null) ? 43 : $translations.hashCode());
            return result;
        }
        
        @Override
        public String toString() {
            return "TranslationRegistry.JsonProvider()";
        }
    }
    
    private interface TranslationProvider
    {
        String translate(final String p0);
    }
}
