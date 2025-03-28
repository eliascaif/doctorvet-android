package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Product implements Serializable, DoctorVetApp.IResourceObject {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private Product_unit unit;
    @Expose
    private BigDecimal quantity;
    @Expose
    private BigDecimal complex_unit_quantity;
    @Expose
    private String bar_code;
    @Expose
    private String qr_code;
    @Expose
    private Integer expires;
    @Expose
    private Integer is_study;
    @Expose
    private Integer service_duration;
    @Expose
    private BigDecimal fixed_p1;
    @Expose
    private BigDecimal margin_p1;
    @Expose
    private DoctorVetApp.products_prices format_p1;
    @Expose
    private BigDecimal fixed_p2;
    @Expose
    private BigDecimal margin_p2;
    @Expose
    private DoctorVetApp.products_prices format_p2;
    @Expose
    private BigDecimal fixed_p3;
    @Expose
    private BigDecimal margin_p3;
    @Expose
    private DoctorVetApp.products_prices format_p3;
    @Expose
    private BigDecimal fixed_p4;
    @Expose
    private BigDecimal margin_p4;
    @Expose
    private DoctorVetApp.products_prices format_p4;
    @Expose
    private BigDecimal cost;
    @Expose
    private BigDecimal tax;
    @Expose
    private BigDecimal min_quantity;
    @Expose
    private ArrayList<Product_category> categories;
    @Expose
    private Boolean is_service;
    @Expose
    private ArrayList<Resource> resources;
    @Expose
    private Integer photo_deleted;

    private Boolean is_global;
    private String photo_url;
    private String thumb_url;
    private Integer deleted;
    private String notes;
    private String quantity_string;
    private Integer is_associate_with_vet;
    private String vet_issued_name;
    private BigDecimal p1;
    private BigDecimal p2;
    private BigDecimal p3;
    private BigDecimal p4;
    private ArrayList<Product_qty_detail> quantity_detail;
    private ArrayList<Product_qty_detail> quantity_detail_branchs;
    private Integer id_brand;
    private String name_brand;
    private Integer id_line;
    private String name_line;
    private Integer id_especies;
    private String name_especies;
    private Integer id_stage;
    private String name_stage;
    private Integer id_size;
    private String name_size;
    private Integer id_flavor;
    private String name_flavor;
    private Integer id_packaging;
    private String name_packaging;
    private Integer id_quantity;
    private String name_quantity;
    private Integer id_manufacturer;
    private String name_manufacturer;
    private String short_name;
    private Integer duration;
    private Integer id_category;
    private Integer service_assoc;

    public Product() {
    }
    public Product(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    public Product(Integer id, String name, String thumb_url) {
        this.id = id;
        this.name = name;
        this.thumb_url = thumb_url;
    }

    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public String getQuantity_string() {
        return quantity_string;
    }
    public ArrayList<Product_qty_detail> getQuantity_detail() {
        return quantity_detail;
    }
    public ArrayList<Product_qty_detail> getQuantity_detail_branchs() {
        return quantity_detail_branchs;
    }
    public BigDecimal getP1() {
        return p1;
    }

    public BigDecimal getP2() {
        return p2;
    }
    public BigDecimal getP3() {
        return p3;
    }
    public BigDecimal getP4() {
        return p4;
    }
    public Product_unit getUnit() {
        return unit;
    }
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }
    public String getNotas() {
        return notes;
    }
    public void setNotas(String notes) {
        this.notes = notes;
    }
    public Boolean getIs_service() {
        return is_service;
    }
    public void setIs_service(Boolean is_service) {
        this.is_service = is_service;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getComplex_unit_quantity() {
        return complex_unit_quantity;
    }
    public String getBar_code() {
        return bar_code;
    }
    public Integer getExpires() {
        return expires;
    }
    public void setExpires(Integer expires) {
        this.expires = expires;
    }
    public Integer getIs_study() {
        return is_study;
    }
    public void setIs_study(Integer is_study) {
        this.is_study = is_study;
    }
    public Boolean getIs_global() {
        return is_global;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public DoctorVetApp.products_prices getFormat_p1() {
        return format_p1;
    }
    public void setFormat_p1(DoctorVetApp.products_prices format_p1) {
        this.format_p1 = format_p1;
    }
    public DoctorVetApp.products_prices getFormat_p2() {
        return format_p2;
    }
    public void setFormat_p2(DoctorVetApp.products_prices format_p2) {
        this.format_p2 = format_p2;
    }
    public DoctorVetApp.products_prices getFormat_p3() {
        return format_p3;
    }
    public void setFormat_p3(DoctorVetApp.products_prices format_p3) {
        this.format_p3 = format_p3;
    }
    public BigDecimal getCost() {
        return cost;
    }
    public BigDecimal getTax() {
        return tax;
    }
    public BigDecimal getMin_quantity() {
        return min_quantity;
    }
    public Integer getId_manufacturer() {
        return id_manufacturer;
    }
    public String getShort_name() {
        if (short_name == null)
            return getName();

        return short_name;
    }
    public void setShort_name(String name_corto) {
        this.short_name = name_corto;
    }
    public Integer getDuration() {
        return duration;
    }
    public String getCategoriesNames(){
        String categoryNamesInStr = "";
        for (Product_category category:getCategories())
            categoryNamesInStr += category.getName() + ", ";
        categoryNamesInStr = categoryNamesInStr.substring(0, categoryNamesInStr.length()-2);
        return categoryNamesInStr;
//        String categorias = "";
//        for (String categoria:this.categories_hashmap.values())
//            categorias += categoria + ", ";
//        categorias = categorias.substring(0, categorias.length()-2);
//        return categorias;
    }
    public ArrayList<Product_category> getCategories() {
        if (categories == null)
            categories = new ArrayList<>();

        return categories;
    }
    public void setCategories(ArrayList<Product_category> categories) {
        this.categories = categories;
    }
    public Integer getIsAssociate_with_vet() {
        return is_associate_with_vet;
    }
    public String getVet_issued_name() {
        return vet_issued_name;
    }
    public String getQuantityDetailDescription() {
        String quantityDetail = "";
        if (quantity_detail != null) {
            for (Product_qty_detail qd:quantity_detail) {
                quantityDetail += qd.deposit_name + ": ";
                quantityDetail += qd.quantity.setScale(2, RoundingMode.HALF_UP).toString() + " " + getUnit().getSecond_unit_string() + "\r\n"; //" | ";
            }
        }

        return quantityDetail;
    }
    public String getQuantityDetailBranchsDescription() {
        String quantityDetail = "";
        if (quantity_detail_branchs != null) {
            for (Product_qty_detail qd:quantity_detail_branchs) {
                quantityDetail += qd.deposit_name + ": ";
                quantityDetail += qd.quantity.setScale(2, RoundingMode.HALF_UP).toString() + " " + getUnit().getSecond_unit_string() + "\r\n"; //" | ";
            }
        }

        return quantityDetail;
    }
    public String getRoundedPrice1() {
        if (getP1() == null)
            return null;

        BigDecimal auxHalfUp = getP1().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedPrice2() {
        if (getP2() == null)
            return null;

        BigDecimal auxHalfUp = getP2().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedPrice3() {
        if (getP3() == null)
            return null;

        BigDecimal auxHalfUp = getP3().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedPrice4() {
        if (getP4() == null)
            return null;

        BigDecimal auxHalfUp = getP4().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getFormattedPrice(Integer priceNumber) {
        if (priceNumber == 1 && getP1() != null)
            return DoctorVetApp.get().toCurrency(getP1().setScale(2, RoundingMode.HALF_UP));

        if (priceNumber == 2 && getP2() != null)
            return DoctorVetApp.get().toCurrency(getP2().setScale(2, RoundingMode.HALF_UP));

        if (priceNumber == 3 && getP3() != null)
            return DoctorVetApp.get().toCurrency(getP3().setScale(2, RoundingMode.HALF_UP));

        if (priceNumber == 4 && getP4() != null)
            return DoctorVetApp.get().toCurrency(getP4().setScale(2, RoundingMode.HALF_UP));

        return null;
    }
    public String getRoundedCost() {
        if (getCost() == null)
            return null;

        BigDecimal auxHalfUp = getCost().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedTax() {
        if (getTax() == null)
            return null;

        BigDecimal auxHalfUp = getTax().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedQuantity() {
        if (getQuantity() == null)
            return null;

        BigDecimal auxHalfUp = getQuantity().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedMinQuantity() {
        if (getMin_quantity() == null)
            return null;

        BigDecimal auxHalfUp = getMin_quantity().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getRoundedComplexQuantity() {
        if (getComplex_unit_quantity() == null)
            return null;

        BigDecimal auxHalfUp = getComplex_unit_quantity().setScale(2, RoundingMode.HALF_UP);
        return auxHalfUp.toString();
    }
    public String getPrettyQuantity() {
        String quantity = getRoundedQuantity();
        if (quantity == null)
            quantity = BigDecimal.ZERO.toString();

        String pretty_quantity = quantity + " " + getUnit().getName();
        return pretty_quantity;
    }
    public String getPrices() {
        String prices = "";
        if (getP1() != null /*&& getP1().compareTo(BigDecimal.ZERO) != 0*/)
            prices = getRoundedPrice1();

        if (getP2() != null /*&& getP2().compareTo(BigDecimal.ZERO) != 0*/)
            prices += " | " + getRoundedPrice2();

        if (getP3() != null /*&& getP3().compareTo(BigDecimal.ZERO) != 0*/)
            prices += " | " + getRoundedPrice3();

        if (getP4() != null /*&& getP4().compareTo(BigDecimal.ZERO) != 0*/)
            prices += " | " + getRoundedPrice4();

        //String prices = getRoundedPrice1() + " | " + getRoundedPrice2() + " | " + getRoundedPrice3() + " | " + getRoundedPrice4();
        return prices;
    }
    public String getFormattedPrices() {
        String prices = "";
        if (getP1() != null)
            prices = getFormattedPrice(1);

        if (getP2() != null)
            prices += " | " + getFormattedPrice(2);

        if (getP3() != null)
            prices += " | " + getFormattedPrice(3);

        if (getP4() != null)
            prices += " | " + getFormattedPrice(4);

        return prices;
    }
    public Boolean hasPrices() {
        return (
                getP1() != null ||
                        getP2() != null ||
                        getP3() != null ||
                        getP4() != null
        );
//        return (
//                getP1().compareTo(BigDecimal.ZERO) != 0 ||
//                getP2().compareTo(BigDecimal.ZERO) != 0 ||
//                getP3().compareTo(BigDecimal.ZERO) != 0 ||
//                getP4().compareTo(BigDecimal.ZERO) != 0
//                );
    }
    public void setUnit(Product_unit unit) {
        this.unit = unit;
    }
    public Integer getPhoto_deleted() {
        return photo_deleted;
    }
    public String getThumb_url() {
        return thumb_url;
    }
    public Integer getService_assoc() {
        return service_assoc;
    }
    public void setService_assoc(Integer service_assoc) {
        this.service_assoc = service_assoc;
    }
    public Product getPolish() {
        Product product = new Product(id, name, thumb_url);
        return product;
    }

    @Override
    public String getPhoto_url() {
        return photo_url;
    }

    @Override
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    @Override
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    @Override
    public void setThumb_deleted(Integer photo_deleted) {
        this.photo_deleted = photo_deleted;
    }

    @Override
    public String toString() {
        return getName();
    }

    public class Get_pagination_products extends Get_pagination {
        @Expose
        private ArrayList<Product> content;

        public ArrayList<Product> getContent() {
            return content;
        }
        public void setContent(ArrayList<Product> content) {
            this.content = content;
        }
    }

    public class Products_for_input {
        private ArrayList<Product_category> products_categories;
        private ArrayList<Product_unit> products_units;

        public ArrayList<Product_category> getProducts_categories() {
            return products_categories;
        }

        public void setProducts_categories(ArrayList<Product_category> products_categories) {
            this.products_categories = products_categories;
        }

        public ArrayList<Product_unit> getProducts_units() {
            return products_units;
        }

        public void setProducts_units(ArrayList<Product_unit> products_units) {
            this.products_units = products_units;
        }
    }

    public class Services_for_input {
        private ArrayList<Product_category> products_categories;

        public ArrayList<Product_category> getProducts_categories() {
            return products_categories;
        }

        public void setProducts_categories(ArrayList<Product_category> products_categories) {
            this.products_categories = products_categories;
        }
    }

    public class Product_qty_detail {
        private String deposit_name;
        private BigDecimal quantity;

        public String getDeposit_name() {
            return deposit_name;
        }

        public void setDeposit_name(String deposit_name) {
            this.deposit_name = deposit_name;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }
    }

}