package com.example.ccm;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Guideline;

import com.example.ccm.calc.CreditCalculator;
import com.example.ccm.calc.MoneyText;
import com.example.ccm.calc.CreditResult;
import com.example.ccm.calc.DepositCalculator;
import com.example.ccm.calc.DepositResult;
import com.example.ccm.calc.MortgageCalculator;
import com.example.ccm.calc.SavingsCalculator;
import com.example.ccm.data.Product;
import com.example.ccm.data.MortgageRateRule;
import com.example.ccm.data.RateRule;
import com.example.ccm.data.Repository;
import com.example.ccm.data.VtbDepositSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BankDetailActivity extends BaseActivity {

    private static final int COLOR_HINT = 0xFF666666;
    private static final int SEEK_MAX = 1000;
    private static final double[] SBER_AMOUNT_TICKS = {10_000d, 100_000d, 2_000_000d, 20_000_000d};
    private static final int[] SBER_AMOUNT_TICK_PROGRESS = {0, 303, 697, SEEK_MAX};
    private static final double SBER_DEPOSIT_MIN_AMOUNT = 100_000d;
    private static final double SBER_DEPOSIT_MID_AMOUNT = 500_000_000d;
    private static final double SBER_DEPOSIT_MAX_AMOUNT = 1_000_000_000d;
    private static final int SBER_DEPOSIT_MID_PROGRESS = SEEK_MAX / 2;
    private static final double SBER_MORTGAGE_MIN_AMOUNT = 376_000d;
    private static final double SBER_MORTGAGE_MID_AMOUNT = 500_000_000d;
    private static final double SBER_MORTGAGE_MAX_AMOUNT = 1_000_000_000d;
    private static final int SBER_MORTGAGE_MID_PROGRESS = SEEK_MAX / 2;
    private static final int SBER_MORTGAGE_MIN_TERM = 12;
    private static final int SBER_MORTGAGE_MID_TERM = 180;
    private static final int SBER_MORTGAGE_MAX_TERM = 360;
    private static final int SBER_MORTGAGE_TERM_MID_PROGRESS = SEEK_MAX / 2;
    private static final int[] SBER_DEPOSIT_TERMS = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 18, 24, 36
    };
    private static final int[] SBER_DEPOSIT_TERM_ANCHOR_PROGRESS = {0, 333, 667, SEEK_MAX};
    private static final int[] SBER_DEPOSIT_TERM_ANCHOR_INDEX = {0, 5, 11, 14};
    private static final int[] VTB_DEPOSIT_TERMS = {3, 4, 6, 12, 18, 24, 36};

    private SwitchCompat switchManualRate;
    private EditText etManualRate;
    private TextView tvRateSource;

    private double combinedLimit, combinedSubsidizedRate, combinedMarketRate;
    private Repository repo;
    private Product product;
    private String type;
    private long bankId;
    private String bankName;
    private int bankAccentColor = 0xFF21A038;

    private EditText etAmount, etTerm, etRate;
    private TextView tvResult, tvWarning;
    private TextView[] amountTickViews;
    private Guideline[] amountTickGuides;
    private Guideline[] termTickGuides;
    private TextView[] termTickViews;
    private SeekBar seekAmount, seekTerm;
    private View salaryClientOption;
    private SwitchCompat switchSalaryClient;
    private View vtbCreditOptionsSection;
    private SwitchCompat switchVtbSalaryClient, switchVtbPensioner;
    private boolean updatingVtbOptions = false;
    private View depositOptionsSection;
    private SwitchCompat switchDepositTopUp, switchDepositSalary;
    private SwitchCompat switchDepositSpending, switchDepositBalance;
    private Spinner spinnerDepositPayout;
    private boolean depositControlsReady = false;

    private View vtbDepositOptionsSection, vtbTermDepositControls, vtbAccountControls;
    private Spinner spinnerVtbDepositProduct, spinnerVtbDepositOperation, spinnerVtbDepositChannel;
    private Spinner spinnerVtbDepositPayout, spinnerVtbDepositBalance, spinnerVtbDepositPackage;
    private Spinner spinnerVtbDepositSpending, spinnerVtbDepositRasti;
    private SwitchCompat switchVtbDepositSalary, switchVtbDepositPension, switchVtbDepositPlus;
    private SwitchCompat switchVtbDepositNewMoney, switchVtbDepositNewClient, switchVtbAccountWelcome;
    private TextView tvTermLabel, tvRateLabel;
    private boolean vtbDepositControlsReady = false, updatingVtbDepositOptions = false;
    private VtbDepositSettings vtbDepositSettings;

    private TextView tvAmountLabel, tvDownPaymentPercent, tvDownPaymentLabel;
    private View mortgageOptionsSection, mortgageFamilySection, mortgageStandardSection, mortgageITSection;
    private Spinner spinnerMortgageCity, spinnerMortgageProgram, spinnerMortgageChildren;
    private EditText etDownPayment, etMortgageExtraCosts, etMortgageMaternalCapital;
    private SeekBar seekDownPayment;
    private View mortgageExtraCostsSection, mortgageMaternalCapitalSection;
    private Button btnMortgageAddCosts, btnMortgageNoDownPayment;
    private RadioGroup rgMortgageVariant;
    private RadioButton rbMortgageBase, rbMortgageMilitary;
    private Spinner spinnerMortgagePersonalDiscount;
    private SwitchCompat switchMortgageChildUnder7, switchMortgageTwoUnder18, switchMortgageDisabledChild;
    private SwitchCompat switchMortgageSalary, switchMortgageApprovedTag;
    private SwitchCompat switchMortgageGuarantee, switchMortgageOnlineDeal;
    private SwitchCompat switchMortgageMaternalCapital;
    private SwitchCompat switchMortgageITEligible;
    private View vtbMortgageOptionsSection;
    private SwitchCompat switchVtbMortgageCurrentSalary, switchVtbMortgageNewSalary;
    private SwitchCompat switchVtbMortgageOnline, switchVtbMortgageInsurance;
    private SwitchCompat switchVtbMortgageProjectFunding;
    private boolean updatingVtbMortgageOptions = false;
    private boolean mortgageControlsReady = false;
    private boolean updatingDownPayment = false;
    private boolean mortgageExtraCostsExpanded = false;

    private boolean updatingRanges;
    private boolean restoringState;
    private boolean mortgageAmountAvailable = true;
    private double minAmount = 1;
    private double maxAmount = 1;
    private int minTerm = 1;
    private int maxTerm = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra("type");
        bankId = getIntent().getLongExtra("bankId", -1);
        bankName = getIntent().getStringExtra("bankName");
        String colorHex = getIntent().getStringExtra("color");
        if (colorHex == null) colorHex = "#D9D9D9";

        try {
            bankAccentColor = Color.parseColor(colorHex);
        } catch (IllegalArgumentException ignored) {
            bankAccentColor = 0xFF21A038;
        }
        setFramedContent(R.layout.content_detail, bankAccentColor);

        ((TextView) findViewById(R.id.tvBank)).setText(bankName);
        ((TextView) findViewById(R.id.tvType)).setText(type);

        repo = new Repository(this);
        product = repo.getProduct(bankId, type);
        vtbDepositSettings = product == null ? null : repo.getVtbDepositSettings(product.id);

        etAmount = findViewById(R.id.etAmount);
        etTerm = findViewById(R.id.etTerm);
        etRate = findViewById(R.id.etRate);
        tvResult = findViewById(R.id.tvResult);
        tvWarning = findViewById(R.id.tvWarning);
        switchManualRate = findViewById(R.id.switchManualRate);
        etManualRate = findViewById(R.id.etManualRate);
        tvRateSource = findViewById(R.id.tvRateSource);
        tvRateSource.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        switchManualRate.setOnCheckedChangeListener((button, checked) -> {
            etManualRate.setVisibility(checked ? View.VISIBLE : View.GONE);
            if (depositControlsReady) onAmountOrTermChanged();
        });
        etManualRate.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (depositControlsReady && switchManualRate.isChecked()) onAmountOrTermChanged();
            }
        });
        seekAmount = findViewById(R.id.seekAmount);
        seekTerm = findViewById(R.id.seekTerm);
        salaryClientOption = findViewById(R.id.salaryClientOption);
        switchSalaryClient = findViewById(R.id.switchSalaryClient);
        vtbCreditOptionsSection = findViewById(R.id.vtbCreditOptionsSection);
        switchVtbSalaryClient = findViewById(R.id.switchVtbSalaryClient);
        switchVtbPensioner = findViewById(R.id.switchVtbPensioner);
        depositOptionsSection = findViewById(R.id.depositOptionsSection);
        switchDepositTopUp = findViewById(R.id.switchDepositTopUp);
        switchDepositSalary = findViewById(R.id.switchDepositSalary);
        switchDepositSpending = findViewById(R.id.switchDepositSpending);
        switchDepositBalance = findViewById(R.id.switchDepositBalance);
        spinnerDepositPayout = findViewById(R.id.spinnerDepositPayout);
        vtbDepositOptionsSection = findViewById(R.id.vtbDepositOptionsSection);
        vtbTermDepositControls = findViewById(R.id.vtbTermDepositControls);
        vtbAccountControls = findViewById(R.id.vtbAccountControls);
        spinnerVtbDepositProduct = findViewById(R.id.spinnerVtbDepositProduct);
        spinnerVtbDepositOperation = findViewById(R.id.spinnerVtbDepositOperation);
        spinnerVtbDepositChannel = findViewById(R.id.spinnerVtbDepositChannel);
        spinnerVtbDepositPayout = findViewById(R.id.spinnerVtbDepositPayout);
        spinnerVtbDepositBalance = findViewById(R.id.spinnerVtbDepositBalance);
        spinnerVtbDepositPackage = findViewById(R.id.spinnerVtbDepositPackage);
        spinnerVtbDepositSpending = findViewById(R.id.spinnerVtbDepositSpending);
        spinnerVtbDepositRasti = findViewById(R.id.spinnerVtbDepositRasti);
        switchVtbDepositSalary = findViewById(R.id.switchVtbDepositSalary);
        switchVtbDepositPension = findViewById(R.id.switchVtbDepositPension);
        switchVtbDepositPlus = findViewById(R.id.switchVtbDepositPlus);
        switchVtbDepositNewMoney = findViewById(R.id.switchVtbDepositNewMoney);
        switchVtbDepositNewClient = findViewById(R.id.switchVtbDepositNewClient);
        switchVtbAccountWelcome = findViewById(R.id.switchVtbAccountWelcome);

        tvAmountLabel = findViewById(R.id.tvAmountLabel);
        tvTermLabel = findViewById(R.id.tvTermLabel);
        tvRateLabel = findViewById(R.id.tvRateLabel);
        mortgageOptionsSection = findViewById(R.id.mortgageOptionsSection);
        mortgageFamilySection = findViewById(R.id.mortgageFamilySection);
        mortgageStandardSection = findViewById(R.id.mortgageStandardSection);
        mortgageITSection = findViewById(R.id.mortgageITSection);
        spinnerMortgageCity = findViewById(R.id.spinnerMortgageCity);
        spinnerMortgageProgram = findViewById(R.id.spinnerMortgageProgram);
        spinnerMortgageChildren = findViewById(R.id.spinnerMortgageChildren);
        etDownPayment = findViewById(R.id.etDownPayment);
        tvDownPaymentPercent = findViewById(R.id.tvDownPaymentPercent);
        tvDownPaymentLabel = findViewById(R.id.tvDownPaymentLabel);
        seekDownPayment = findViewById(R.id.seekDownPayment);
        etMortgageExtraCosts = findViewById(R.id.etMortgageExtraCosts);
        etMortgageMaternalCapital = findViewById(R.id.etMortgageMaternalCapital);
        mortgageExtraCostsSection = findViewById(R.id.mortgageExtraCostsSection);
        mortgageMaternalCapitalSection = findViewById(R.id.mortgageMaternalCapitalSection);
        btnMortgageAddCosts = findViewById(R.id.btnMortgageAddCosts);
        btnMortgageNoDownPayment = findViewById(R.id.btnMortgageNoDownPayment);
        rgMortgageVariant = findViewById(R.id.rgMortgageVariant);
        rbMortgageBase = findViewById(R.id.rbMortgageBase);
        rbMortgageMilitary = findViewById(R.id.rbMortgageMilitary);
        spinnerMortgagePersonalDiscount = findViewById(R.id.spinnerMortgagePersonalDiscount);
        switchMortgageChildUnder7 = findViewById(R.id.switchMortgageChildUnder7);
        switchMortgageTwoUnder18 = findViewById(R.id.switchMortgageTwoUnder18);
        switchMortgageDisabledChild = findViewById(R.id.switchMortgageDisabledChild);
        switchMortgageSalary = findViewById(R.id.switchMortgageSalary);
        switchMortgageApprovedTag = findViewById(R.id.switchMortgageApprovedTag);
        switchMortgageGuarantee = findViewById(R.id.switchMortgageGuarantee);
        switchMortgageOnlineDeal = findViewById(R.id.switchMortgageOnlineDeal);
        switchMortgageMaternalCapital = findViewById(R.id.switchMortgageMaternalCapital);
        switchMortgageITEligible = findViewById(R.id.switchMortgageITEligible);
        vtbMortgageOptionsSection = findViewById(R.id.vtbMortgageOptionsSection);
        switchVtbMortgageCurrentSalary = findViewById(R.id.switchVtbMortgageCurrentSalary);
        switchVtbMortgageNewSalary = findViewById(R.id.switchVtbMortgageNewSalary);
        switchVtbMortgageOnline = findViewById(R.id.switchVtbMortgageOnline);
        switchVtbMortgageInsurance = findViewById(R.id.switchVtbMortgageInsurance);
        switchVtbMortgageProjectFunding = findViewById(R.id.switchVtbMortgageProjectFunding);
        MoneyInputFormatter.attach(etAmount);
        MoneyInputFormatter.attach(etDownPayment);
        MoneyInputFormatter.attach(etMortgageExtraCosts);
        MoneyInputFormatter.attach(etMortgageMaternalCapital);

        if (isVtbMortgage()) {
            ArrayAdapter<CharSequence> mortgageAdapter = ArrayAdapter.createFromResource(
                    this, R.array.vtb_mortgage_program_options, android.R.layout.simple_spinner_item);
            mortgageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMortgageProgram.setAdapter(mortgageAdapter);
        }

        if (isTbankMortgage()) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.tbank_mortgage_program_options, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMortgageProgram.setAdapter(adapter);
        }
        amountTickViews = new TextView[]{
                findViewById(R.id.tvAmountTick1), findViewById(R.id.tvAmountTick2),
                findViewById(R.id.tvAmountTick3), findViewById(R.id.tvAmountTick4)
        };
        amountTickGuides = new Guideline[]{
                findViewById(R.id.amountTickGuide2), findViewById(R.id.amountTickGuide3)
        };
        termTickGuides = new Guideline[]{
                findViewById(R.id.termTickGuide2), findViewById(R.id.termTickGuide3)
        };
        termTickViews = new TextView[]{
                findViewById(R.id.tvTermTick1), findViewById(R.id.tvTermTick2),
                findViewById(R.id.tvTermTick3), findViewById(R.id.tvTermTick4)
        };


        etRate.setKeyListener(null);
        etRate.setFocusable(false);
        etRate.setFocusableInTouchMode(false);
        etRate.setClickable(false);
        etRate.setLongClickable(false);
        etRate.setCursorVisible(false);

        salaryClientOption.setVisibility(isSberCredit() ? View.VISIBLE : View.GONE);
        vtbCreditOptionsSection.setVisibility(isVtbCredit() ? View.VISIBLE : View.GONE);
        depositOptionsSection.setVisibility(isSberDeposit() ? View.VISIBLE : View.GONE);
        vtbDepositOptionsSection.setVisibility(isVtbDeposit() ? View.VISIBLE : View.GONE);
        switchVtbAccountWelcome.setVisibility(View.GONE);
        mortgageOptionsSection.setVisibility(isAnyMortgage() ? View.VISIBLE : View.GONE);
        if (isAnyMortgage()) {
            tvAmountLabel.setText("Стоимость недвижимости, ₽");
            etAmount.setText("5241286");
            etTerm.setText("240");
            etDownPayment.setText("1053698");
            if (isTbankMortgage()) {
                etAmount.setText("3500000");
                etDownPayment.setText("703500");
                etTerm.setText("360");
            }
            spinnerMortgageCity.setSelection(0);
            spinnerMortgageProgram.setSelection(0);
            spinnerMortgageChildren.setSelection(0);
            switchMortgageChildUnder7.setChecked(false);
            switchMortgageTwoUnder18.setChecked(false);
            switchMortgageDisabledChild.setChecked(false);
            switchMortgageSalary.setChecked(isSberMortgage());
            switchMortgageApprovedTag.setChecked(false);
            switchMortgageGuarantee.setChecked(false);
            switchMortgageOnlineDeal.setChecked(false);
            switchMortgageMaternalCapital.setChecked(false);
            switchMortgageITEligible.setChecked(false);
            rbMortgageBase.setChecked(true);
            rbMortgageMilitary.setChecked(false);
            spinnerMortgagePersonalDiscount.setSelection(0);
            etMortgageExtraCosts.setText("0");
            etMortgageMaternalCapital.setText("0");
            switchVtbMortgageCurrentSalary.setChecked(false);
            switchVtbMortgageNewSalary.setChecked(false);
            switchVtbMortgageOnline.setChecked(false);
            switchVtbMortgageInsurance.setChecked(true);
            switchVtbMortgageProjectFunding.setChecked(true);
        }
        updateMortgageOptionVisibility();
        switchSalaryClient.setChecked(false);
        switchVtbSalaryClient.setChecked(false);
        switchVtbPensioner.setChecked(false);
        switchDepositTopUp.setChecked(false);
        switchDepositSalary.setChecked(false);
        switchDepositSpending.setChecked(false);
        switchDepositBalance.setChecked(false);
        spinnerDepositPayout.setSelection(0);
        spinnerVtbDepositProduct.setSelection(0);
        spinnerVtbDepositOperation.setSelection(0);
        spinnerVtbDepositChannel.setSelection(0);
        spinnerVtbDepositPayout.setSelection(0);
        spinnerVtbDepositBalance.setSelection(0);
        spinnerVtbDepositPackage.setSelection(0);
        spinnerVtbDepositSpending.setSelection(0);
        spinnerVtbDepositRasti.setSelection(0);
        applyBankAccentColor();

        if (!isAnyMortgage()) {
            etAmount.setText(isVtbCredit() ? "30000" : isVtbDeposit() ? "10000" : "100000");
            etTerm.setText(isVtbCredit() ? "45" : "12");
        } else if (isVtbMortgage()) {
            tvAmountLabel.setText("Стоимость недвижимости, ₽");
            tvTermLabel.setText("Срок ипотеки, мес.");
            tvRateLabel.setText("Процентная ставка, % годовых");
        }

        if (isVtbDeposit()) {
            tvRateLabel.setText("Ставка в модели, % годовых");
            tvAmountLabel.setText("Сумма вклада, ₽");
            tvTermLabel.setText("Срок вклада, мес.");
        }
        updateSliderRanges();
        configureSeekBars();
        View.OnFocusChangeListener commitInput = (view, focused) -> {
            if (!focused && !restoringState) {
                updateSliderRanges();
                onAmountOrTermChanged();
            }
        };
        etTerm.setOnFocusChangeListener(commitInput);
        etAmount.setOnFocusChangeListener(commitInput);
        etDownPayment.setOnFocusChangeListener(commitInput);
        etMortgageExtraCosts.setOnFocusChangeListener(commitInput);
        etMortgageMaternalCapital.setOnFocusChangeListener(commitInput);

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                syncAmountSliderFromInput();
                if (isAnyMortgage()) syncDownPaymentSliderFromInput();
                onAmountOrTermChanged();
            }
        });

        etTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (bankId == 3 && !isAnyMortgage()) updateSliderRanges();
                syncTermSliderFromInput();
                onAmountOrTermChanged();
            }
        });

        if (isAnyMortgage()) {
            etDownPayment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (updatingDownPayment || !mortgageControlsReady) return;
                    syncDownPaymentSliderFromInput();
                    refreshMortgageCalculation();
                }
            });

            TextWatcher mortgageExtraWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mortgageControlsReady) refreshMortgageCalculation();
                }
            };
            etMortgageExtraCosts.addTextChangedListener(mortgageExtraWatcher);
            etMortgageMaternalCapital.addTextChangedListener(mortgageExtraWatcher);

            btnMortgageAddCosts.setOnClickListener(v -> {
                mortgageExtraCostsExpanded = !mortgageExtraCostsExpanded;
                boolean show = mortgageExtraCostsExpanded;
                mortgageExtraCostsSection.setVisibility(show ? View.VISIBLE : View.GONE);
                btnMortgageAddCosts.setText(show ? "Скрыть дополнительные расходы" : "Добавить расходы в кредит");
                if (!show) setEditTextValue(etMortgageExtraCosts, "0");
                refreshMortgageCalculation();
            });
            btnMortgageNoDownPayment.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Ипотека без первоначального взноса")
                    .setMessage("В справочной модели для выбранных программ предусмотрен первоначальный взнос. Возможность оформления без него зависит от индивидуального предложения банка и здесь отдельно не рассчитывается.")
                    .setPositiveButton("Понятно", null)
                    .show());

            seekDownPayment.setMax(SEEK_MAX);
            seekDownPayment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    double propertyPrice = parseDouble(etAmount.getText().toString());
                    double minPercent = getMortgageMinimumDownPaymentPercent();
                    double percent = minPercent + ((80.0 - minPercent) * progress / (double) SEEK_MAX);
                    double maternalCapital = switchMortgageMaternalCapital.isChecked()
                            ? parseDouble(etMortgageMaternalCapital.getText().toString()) : 0.0;
                    long downPayment = Math.max(0L, (long) Math.ceil(propertyPrice * percent / 100.0 - maternalCapital));
                    updatingDownPayment = true;
                    setEditTextValue(etDownPayment, String.valueOf(downPayment));
                    updatingDownPayment = false;
                    syncDownPaymentSliderFromInput();
                    if (mortgageControlsReady) refreshMortgageCalculation();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            AdapterView.OnItemSelectedListener mortgageSelectionListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!mortgageControlsReady) return;
                    updateMortgageOptionVisibility();
                    updateSliderRanges();
                    refreshMortgageCalculation();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
            spinnerMortgageCity.setOnItemSelectedListener(mortgageSelectionListener);
            spinnerMortgageProgram.setOnItemSelectedListener(mortgageSelectionListener);
            spinnerMortgageChildren.setOnItemSelectedListener(mortgageSelectionListener);
            spinnerMortgagePersonalDiscount.setOnItemSelectedListener(mortgageSelectionListener);
            rgMortgageVariant.setOnCheckedChangeListener((group, checkedId) -> {
                if (mortgageControlsReady) {
                    updateMortgageOptionVisibility();
                    refreshMortgageCalculation();
                }
            });

            android.widget.CompoundButton.OnCheckedChangeListener mortgageSwitchListener =
                    (buttonView, isChecked) -> {
                        if (mortgageControlsReady) refreshMortgageCalculation();
                    };
            android.widget.CompoundButton.OnCheckedChangeListener vtbCombinedFamilyListener =
                    (buttonView, isChecked) -> {
                        if (isVtbMortgage() && "VTB_COMBINED".equals(getMortgageProgramCode())
                                && isChecked && switchMortgageITEligible.isChecked()) {
                            switchMortgageITEligible.setChecked(false);
                        }
                        if (mortgageControlsReady) {
                            updateSliderRanges();
                            refreshMortgageCalculation();
                        }
                    };
            switchMortgageChildUnder7.setOnCheckedChangeListener(vtbCombinedFamilyListener);
            switchMortgageDisabledChild.setOnCheckedChangeListener(vtbCombinedFamilyListener);
            switchMortgageTwoUnder18.setOnCheckedChangeListener(mortgageSwitchListener);
            switchMortgageSalary.setOnCheckedChangeListener(mortgageSwitchListener);
            switchMortgageApprovedTag.setOnCheckedChangeListener(mortgageSwitchListener);
            switchMortgageGuarantee.setOnCheckedChangeListener(mortgageSwitchListener);
            switchMortgageOnlineDeal.setOnCheckedChangeListener(mortgageSwitchListener);
            switchMortgageMaternalCapital.setOnCheckedChangeListener((buttonView, checked) -> {
                mortgageMaternalCapitalSection.setVisibility(checked ? View.VISIBLE : View.GONE);
                syncDownPaymentSliderFromInput();
                if (mortgageControlsReady) refreshMortgageCalculation();
            });
            switchMortgageITEligible.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isVtbMortgage() && "VTB_COMBINED".equals(getMortgageProgramCode()) && isChecked) {
                    switchMortgageChildUnder7.setChecked(false);
                    switchMortgageDisabledChild.setChecked(false);
                }
                if (mortgageControlsReady) {
                    updateSliderRanges();
                    refreshMortgageCalculation();
                }
            });
            android.widget.CompoundButton.OnCheckedChangeListener vtbMortgageListener =
                    (buttonView, isChecked) -> {
                        if (!isVtbMortgage() || updatingVtbMortgageOptions) return;
                        updatingVtbMortgageOptions = true;
                        if (isChecked && buttonView == switchVtbMortgageCurrentSalary) {
                            switchVtbMortgageNewSalary.setChecked(false);
                        } else if (isChecked && buttonView == switchVtbMortgageNewSalary) {
                            switchVtbMortgageCurrentSalary.setChecked(false);
                        }
                        updatingVtbMortgageOptions = false;
                        if (mortgageControlsReady) refreshMortgageCalculation();
                    };
            switchVtbMortgageCurrentSalary.setOnCheckedChangeListener(vtbMortgageListener);
            switchVtbMortgageNewSalary.setOnCheckedChangeListener(vtbMortgageListener);
            switchVtbMortgageOnline.setOnCheckedChangeListener(mortgageSwitchListener);
            switchVtbMortgageInsurance.setOnCheckedChangeListener(mortgageSwitchListener);
            switchVtbMortgageProjectFunding.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mortgageControlsReady) {
                    updateSliderRanges();
                    refreshMortgageCalculation();
                }
            });
            mortgageControlsReady = true;
        }

        switchSalaryClient.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSliderRanges();
            applyRateFromDb();
            updateLimitsWarning();
            recalculate();
        });

        android.widget.CompoundButton.OnCheckedChangeListener vtbOptionListener =
                (buttonView, isChecked) -> {
                    if (!isVtbCredit() || updatingVtbOptions) return;
                    updatingVtbOptions = true;
                    if (isChecked) {
                        if (buttonView == switchVtbSalaryClient)
                            switchVtbPensioner.setChecked(false);
                        else if (buttonView == switchVtbPensioner)
                            switchVtbSalaryClient.setChecked(false);
                    }
                    updatingVtbOptions = false;
                    refreshVtbCreditConditions();
                };
        switchVtbSalaryClient.setOnCheckedChangeListener(vtbOptionListener);
        switchVtbPensioner.setOnCheckedChangeListener(vtbOptionListener);

        android.widget.CompoundButton.OnCheckedChangeListener depositOptionListener =
                (buttonView, isChecked) -> {
                    if (depositControlsReady) refreshDepositCalculation();
                };
        switchDepositTopUp.setOnCheckedChangeListener(depositOptionListener);
        switchDepositSalary.setOnCheckedChangeListener(depositOptionListener);
        switchDepositSpending.setOnCheckedChangeListener(depositOptionListener);
        switchDepositBalance.setOnCheckedChangeListener(depositOptionListener);
        spinnerDepositPayout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (depositControlsReady) refreshDepositCalculation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        depositControlsReady = true;

        android.widget.CompoundButton.OnCheckedChangeListener vtbDepositSwitchListener =
                (buttonView, isChecked) -> {
                    if (!isVtbDeposit() || updatingVtbDepositOptions) return;
                    updatingVtbDepositOptions = true;
                    if (isChecked) {
                        SwitchCompat[] exclusiveBenefits = new SwitchCompat[]{
                                switchVtbDepositSalary, switchVtbDepositPension,
                                switchVtbDepositPlus, switchVtbDepositNewMoney,
                                switchVtbDepositNewClient};
                        for (SwitchCompat option : exclusiveBenefits) {
                            if (option != buttonView) option.setChecked(false);
                        }
                        if (buttonView == switchVtbDepositNewMoney || buttonView == switchVtbDepositNewClient) {
                            spinnerVtbDepositRasti.setSelection(0);
                        }
                    }
                    updatingVtbDepositOptions = false;
                    syncVtbRastiAvailability();
                    refreshVtbDepositCalculation();
                };
        switchVtbDepositSalary.setOnCheckedChangeListener(vtbDepositSwitchListener);
        switchVtbDepositPension.setOnCheckedChangeListener(vtbDepositSwitchListener);
        switchVtbDepositPlus.setOnCheckedChangeListener(vtbDepositSwitchListener);
        switchVtbDepositNewMoney.setOnCheckedChangeListener(vtbDepositSwitchListener);
        switchVtbDepositNewClient.setOnCheckedChangeListener(vtbDepositSwitchListener);
        switchVtbAccountWelcome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isVtbDeposit() && !updatingVtbDepositOptions) refreshVtbDepositCalculation();
        });

        AdapterView.OnItemSelectedListener vtbDepositSelectionListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!vtbDepositControlsReady || updatingVtbDepositOptions) return;
                if (parent == spinnerVtbDepositProduct) {
                    boolean accountMode = position == 1;
                    vtbTermDepositControls.setVisibility(accountMode ? View.GONE : View.VISIBLE);
                    vtbAccountControls.setVisibility(accountMode ? View.VISIBLE : View.GONE);
                    tvAmountLabel.setText(accountMode ? "Сумма накопительного счёта, ₽" : "Сумма вклада, ₽");
                    tvTermLabel.setText(accountMode ? "Период расчёта дохода, мес." : "Срок вклада, мес.");
                    tvRateLabel.setText(accountMode ? "Расчётная ставка, % годовых" : "Ставка в модели, % годовых");
                    switchVtbDepositNewMoney.setVisibility(accountMode ? View.GONE : View.VISIBLE);
                    switchVtbDepositNewClient.setVisibility(accountMode ? View.GONE : View.VISIBLE);
                    if (accountMode) {
                        switchVtbAccountWelcome.setVisibility(View.VISIBLE);
                    } else {
                        switchVtbAccountWelcome.setVisibility(View.GONE);
                    }
                }
                if (parent == spinnerVtbDepositChannel) {
                    double newMin = position == 0 ? vtbSettingMinOnline() : vtbSettingMinOffice();
                    double current = parseDouble(etAmount.getText().toString());
                    if (current > 0 && current < newMin)
                        setEditTextValue(etAmount, String.valueOf((long) newMin));
                }
                syncVtbRastiAvailability();
                refreshVtbDepositCalculation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerVtbDepositProduct.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositOperation.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositChannel.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositPayout.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositBalance.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositPackage.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositSpending.setOnItemSelectedListener(vtbDepositSelectionListener);
        spinnerVtbDepositRasti.setOnItemSelectedListener(vtbDepositSelectionListener);
        vtbDepositControlsReady = true;
        syncVtbRastiAvailability();

        applyRateFromDb();
        updateLimitsWarning();
        recalculate();
    }


    private void applyBankAccentColor() {
        int uncheckedThumb = 0xFFF7F7F7;
        int uncheckedTrack = 0xFFBDBDBD;
        int checkedTrack = withAlpha(bankAccentColor, 0.38f);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{}
        };
        int[] thumbColors = new int[]{bankAccentColor, uncheckedThumb};
        int[] trackColors = new int[]{checkedTrack, uncheckedTrack};

        ColorStateList thumbTint = new ColorStateList(states, thumbColors);
        ColorStateList trackTint = new ColorStateList(states, trackColors);
        switchSalaryClient.setThumbTintList(thumbTint);
        switchSalaryClient.setTrackTintList(trackTint);
        if (switchVtbSalaryClient != null) {
            switchVtbSalaryClient.setThumbTintList(thumbTint);
            switchVtbSalaryClient.setTrackTintList(trackTint);
            switchVtbPensioner.setThumbTintList(thumbTint);
            switchVtbPensioner.setTrackTintList(trackTint);
        }
        if (rbMortgageBase != null) rbMortgageBase.setButtonTintList(thumbTint);
        if (rbMortgageMilitary != null) rbMortgageMilitary.setButtonTintList(thumbTint);
        SwitchCompat[] mortgageSwitches = new SwitchCompat[]{switchMortgageChildUnder7,
                switchMortgageTwoUnder18, switchMortgageDisabledChild, switchMortgageSalary,
                switchMortgageApprovedTag, switchMortgageGuarantee, switchMortgageOnlineDeal,
                switchMortgageMaternalCapital, switchMortgageITEligible};
        for (SwitchCompat mortgageSwitch : mortgageSwitches) {
            if (mortgageSwitch != null) {
                mortgageSwitch.setThumbTintList(thumbTint);
                mortgageSwitch.setTrackTintList(trackTint);
            }
        }
        if (seekDownPayment != null) {
            seekDownPayment.setProgressTintList(ColorStateList.valueOf(bankAccentColor));
            seekDownPayment.setThumbTintList(ColorStateList.valueOf(bankAccentColor));
            seekDownPayment.setProgressBackgroundTintList(ColorStateList.valueOf(withAlpha(bankAccentColor, 0.22f)));
        }
        if (switchDepositTopUp != null) {
            switchDepositTopUp.setThumbTintList(thumbTint);
            switchDepositTopUp.setTrackTintList(trackTint);
            switchDepositSalary.setThumbTintList(thumbTint);
            switchDepositSalary.setTrackTintList(trackTint);
            switchDepositSpending.setThumbTintList(thumbTint);
            switchDepositSpending.setTrackTintList(trackTint);
            switchDepositBalance.setThumbTintList(thumbTint);
            switchDepositBalance.setTrackTintList(trackTint);
        }

        SwitchCompat[] vtbDepositSwitches = new SwitchCompat[]{switchVtbDepositSalary,
                switchVtbDepositPension, switchVtbDepositPlus, switchVtbDepositNewMoney,
                switchVtbDepositNewClient, switchVtbAccountWelcome};
        for (SwitchCompat vtbSwitch : vtbDepositSwitches) {
            if (vtbSwitch != null) {
                vtbSwitch.setThumbTintList(thumbTint);
                vtbSwitch.setTrackTintList(trackTint);
            }
        }
        seekAmount.setProgressTintList(ColorStateList.valueOf(bankAccentColor));
        seekAmount.setThumbTintList(ColorStateList.valueOf(bankAccentColor));
        seekAmount.setProgressBackgroundTintList(ColorStateList.valueOf(withAlpha(bankAccentColor, 0.22f)));
        seekTerm.setProgressTintList(ColorStateList.valueOf(bankAccentColor));
        seekTerm.setThumbTintList(ColorStateList.valueOf(bankAccentColor));
        seekTerm.setProgressBackgroundTintList(ColorStateList.valueOf(withAlpha(bankAccentColor, 0.22f)));
    }

    private static int withAlpha(int color, float fraction) {
        int alpha = Math.round(Color.alpha(color) * fraction);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }


    private void configureSeekBars() {
        seekAmount.setMax(SEEK_MAX);
        seekTerm.setMax(SEEK_MAX);

        seekAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                long amount = com.example.ccm.calc.AmountScale.wholeRubles(
                        amountForProgress(progress), minAmount, maxAmount);
                setEditTextValue(etAmount, String.valueOf(amount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekTerm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int months = termForProgress(progress);
                setEditTextValue(etTerm, String.valueOf(months));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setEditTextValue(EditText editText, String value) {
        if (value.equals(editText.getText().toString())) return;
        editText.setText(value);
        editText.setSelection(editText.getText().length());
    }


    private void updateSliderRanges() {
        if (updatingRanges || restoringState) return;
        updatingRanges = true;
        try {
            updateSliderRangesInternal();
        } finally {
            updatingRanges = false;
        }
    }

    private void updateSliderRangesInternal() {
        if (isVtbDeposit()) {
            boolean accountMode = isVtbAccountSelected();
            minAmount = accountMode ? vtbSettingMinInterestBalance()
                    : (spinnerVtbDepositChannel.getSelectedItemPosition() == 0
                    ? vtbSettingMinOnline() : vtbSettingMinOffice());
            maxAmount = vtbSettingMaxUiAmount();
            minTerm = accountMode ? 1 : vtbSettingMinTerm();
            maxTerm = vtbSettingMaxTerm(); // срок договора вклада / горизонт прогноза счёта
            double currentAmount = parseDouble(etAmount.getText().toString());
            int currentTerm = parseInt(etTerm.getText().toString());
            if (currentAmount > 0 && currentAmount < minAmount)
                setEditTextValue(etAmount, String.valueOf((long) minAmount));
            else if (currentAmount > maxAmount)
                setEditTextValue(etAmount, String.valueOf((long) maxAmount));
            if (currentTerm > 0 && currentTerm < minTerm)
                setEditTextValue(etTerm, String.valueOf(minTerm));
            else if (currentTerm > maxTerm) setEditTextValue(etTerm, String.valueOf(maxTerm));
            applyAmountMaximumFilter();
            renderSliderTicks();
            syncAmountSliderFromInput();
            syncTermSliderFromInput();
            return;
        }
        if (isAnyMortgage()) {
            minTerm = SBER_MORTGAGE_MIN_TERM;
            maxTerm = SBER_MORTGAGE_MAX_TERM;
            MortgageRateRule rule = activeMortgageRule();
            if (rule == null) {
                minAmount = 1;
                maxAmount = 1;
                mortgageAmountAvailable = false;
            } else {
                boolean withoutDown = isVtbRefinanceProgram(getMortgageProgramCode());
                double down = withoutDown ? 0 : nonNegativeInput(etDownPayment);
                if (!withoutDown && switchMortgageMaternalCapital.isChecked())
                    down += nonNegativeInput(etMortgageMaternalCapital);
                double extra = mortgageExtraCostsSection.getVisibility() == View.VISIBLE ? nonNegativeInput(etMortgageExtraCosts) : 0;
                double loanLimit = isVtbMortgage() ? getVtbMortgageMaximumLoan(rule) : rule.maxLoan;
                minTerm = rule.minTermMonths;
                maxTerm = rule.maxTermMonths;
                if (isVtbMortgage() && ("VTB_FAMILY".equals(getMortgageProgramCode())
                        || ("VTB_COMBINED".equals(getMortgageProgramCode()) && !switchMortgageITEligible.isChecked()))) {
                    maxTerm = Math.min(maxTerm, 180);
                }
                minAmount = Math.max(rule.minPropertyPrice,
                        Math.ceil((isTbankMortgage() ? 500000 : 300000) + down - extra));
                if (!withoutDown) minAmount = Math.max(minAmount, Math.floor(down) + 1);
                double upper = com.example.ccm.calc.MortgageLimits.maximumPrice(loanLimit, down,
                        getMortgageMinimumDownPaymentPercent(), extra, withoutDown);
                mortgageAmountAvailable = upper >= minAmount;
                maxAmount = Math.max(minAmount, upper);
            }
            seekAmount.setEnabled(mortgageAmountAvailable);
            boolean commitAmount = mortgageAmountAvailable && !editingMortgageAmounts();
            double currentAmount = parseDouble(etAmount.getText().toString());
            int currentTerm = parseInt(etTerm.getText().toString());
            if (commitAmount && currentAmount > 0 && currentAmount < minAmount)
                setEditTextValue(etAmount, String.valueOf((long) minAmount));
            else if (commitAmount && currentAmount > maxAmount)
                setEditTextValue(etAmount, String.valueOf((long) maxAmount));
            if (currentTerm > 0 && currentTerm < minTerm)
                setEditTextValue(etTerm, String.valueOf(minTerm));
            else if (currentTerm > maxTerm) setEditTextValue(etTerm, String.valueOf(maxTerm));
            applyAmountMaximumFilter();
            renderSliderTicks();
            syncAmountSliderFromInput();
            syncTermSliderFromInput();
            syncDownPaymentSliderFromInput();
            return;
        }
        List<RateRule> activeRules = getActiveRules();
        if (activeRules.isEmpty()) {
            minAmount = 1;
            maxAmount = 1;
            minTerm = 1;
            maxTerm = 1;
        } else {
            minAmount = Double.MAX_VALUE;
            maxAmount = 0;
            minTerm = Integer.MAX_VALUE;
            maxTerm = 0;
            for (RateRule rule : activeRules) {
                minAmount = Math.min(minAmount, rule.minAmount);
                maxAmount = Math.max(maxAmount, rule.maxAmount);
                minTerm = Math.min(minTerm, rule.minTermMonths);
                maxTerm = Math.max(maxTerm, rule.maxTermMonths);
            }
        }

        if (bankId == 3) {
            int months = parseInt(etTerm.getText().toString());
            double lower = Double.MAX_VALUE, upper = 0;
            for (RateRule rule : activeRules) {
                if (months >= rule.minTermMonths && months <= rule.maxTermMonths) {
                    lower = Math.min(lower, rule.minAmount);
                    upper = Math.max(upper, rule.maxAmount);
                }
            }
            if (upper > 0) {
                minAmount = lower;
                maxAmount = upper;
            }
        }
        if (isVtbCredit() || (bankId == 3 && !etTerm.hasFocus())) {
            double currentAmount = parseDouble(etAmount.getText().toString());
            int currentTerm = parseInt(etTerm.getText().toString());
            if (currentAmount > 0 && currentAmount < minAmount) {
                setEditTextValue(etAmount, String.valueOf((long) minAmount));
            } else if (currentAmount > maxAmount) {
                setEditTextValue(etAmount, String.valueOf((long) maxAmount));
            }
            if (currentTerm > 0 && currentTerm < minTerm) {
                setEditTextValue(etTerm, String.valueOf(minTerm));
            } else if (currentTerm > maxTerm) {
                setEditTextValue(etTerm, String.valueOf(maxTerm));
            }
        }
        applyAmountMaximumFilter();
        renderSliderTicks();
        syncAmountSliderFromInput();
        syncTermSliderFromInput();
    }


    private List<RateRule> getActiveRules() {
        if (product == null) return new ArrayList<>();

        List<RateRule> allRules = repo.getRateRules(product.id);
        if (isTbankCredit()) return allRules;
        String segment = getClientSegment();
        List<RateRule> matching = new ArrayList<>();
        for (RateRule rule : allRules) {
            if (segment.equals(rule.clientSegment)) matching.add(rule);
        }
        if (!matching.isEmpty()) return matching;

        for (RateRule rule : allRules) {
            if ("ALL".equals(rule.clientSegment)) matching.add(rule);
        }
        return matching.isEmpty() ? allRules : matching;
    }


    private double getVtbAmountMidpoint() {
        return maxAmount > 1_000_000.0 ? 20_000_000.0 : 265_000.0;
    }

    private void renderSliderTicks() {
        double[] amountTicks;
        if (isVtbDeposit()) {
            amountTicks = new double[]{minAmount, 500_000d, 5_000_000d, maxAmount};
            amountTickViews[2].setVisibility(View.VISIBLE);
        } else if (isSberCredit()) {
            amountTicks = SBER_AMOUNT_TICKS.clone();
            amountTickViews[2].setVisibility(View.VISIBLE);
        } else if (isVtbCredit()) {
            double middle = getVtbAmountMidpoint();
            amountTicks = new double[]{minAmount, middle, middle, maxAmount};
            amountTickViews[2].setVisibility(View.GONE);
        } else if (isAnyMortgage()) {
            double middle = Math.sqrt(minAmount * maxAmount);
            amountTicks = new double[]{minAmount, middle, middle, maxAmount};
            amountTickViews[2].setVisibility(View.GONE);
        } else if (isSberDeposit()) {
            amountTicks = new double[]{SBER_DEPOSIT_MIN_AMOUNT, SBER_DEPOSIT_MID_AMOUNT,
                    SBER_DEPOSIT_MID_AMOUNT, SBER_DEPOSIT_MAX_AMOUNT};
            amountTickViews[2].setVisibility(View.GONE);
        } else {
            amountTicks = new double[]{
                    amountForFraction(0.0), amountForFraction(1.0 / 3.0),
                    amountForFraction(2.0 / 3.0), amountForFraction(1.0)
            };
            amountTickViews[2].setVisibility(View.VISIBLE);
        }

        setTicks(amountTickViews, new String[]{
                formatAmountTick(amountTicks[0]), formatAmountTick(amountTicks[1]),
                formatAmountTick(amountTicks[2]), formatAmountTick(amountTicks[3])
        });
        amountTickGuides[0].setGuidelinePercent(progressForAmount(amountTicks[1]) / (float) SEEK_MAX);
        amountTickGuides[1].setGuidelinePercent(progressForAmount(amountTicks[2]) / (float) SEEK_MAX);

        termTickViews[1].setVisibility(isVtbMortgage() || isTbankMortgage() ? View.GONE : View.VISIBLE);
        int[] termTicks;
        if (isVtbDeposit()) {
            termTickViews[2].setVisibility(View.VISIBLE);
            termTicks = isVtbAccountSelected() ? new int[]{1, 6, 12, 36}
                    : new int[]{3, 6, 12, 36};
        } else if (isSberMortgage()) {
            termTicks = new int[]{SBER_MORTGAGE_MIN_TERM, SBER_MORTGAGE_MID_TERM,
                    SBER_MORTGAGE_MID_TERM, SBER_MORTGAGE_MAX_TERM};
            // Средняя отметка закреплена ровно на 15 годах; дублирующую подпись скрываем.
            termTickViews[2].setVisibility(View.GONE);
        } else if (isVtbMortgage() || isTbankMortgage()) {
            int middle = Math.max(minTerm, Math.min(maxTerm, (minTerm + maxTerm) / 2));
            termTicks = new int[]{minTerm, middle, middle, maxTerm};
            termTickViews[2].setVisibility(View.GONE);
        } else if (isVtbCredit()) {
            int middle = (minTerm + maxTerm) / 2;
            termTicks = new int[]{minTerm, middle, middle, maxTerm};
            termTickViews[2].setVisibility(View.GONE);
        } else if (isSberCredit()) {
            termTickViews[2].setVisibility(View.VISIBLE);
            termTicks = new int[]{minTerm, 12, 36, maxTerm};
        } else if (isSberDeposit()) {
            termTickViews[2].setVisibility(View.VISIBLE);
            termTicks = new int[]{1, 6, 12, 36};
        } else {
            termTickViews[2].setVisibility(View.VISIBLE);
            termTicks = new int[]{minTerm,
                    (int) Math.round(logInterpolate(minTerm, maxTerm, 1.0 / 3.0)),
                    (int) Math.round(logInterpolate(minTerm, maxTerm, 2.0 / 3.0)),
                    maxTerm};
        }
        setTicks(termTickViews, new String[]{
                formatTermTick(termTicks[0]), formatTermTick(termTicks[1]),
                formatTermTick(termTicks[2]), formatTermTick(termTicks[3])
        });
        termTickGuides[0].setGuidelinePercent(progressForTerm(termTicks[1]) / (float) SEEK_MAX);
        termTickGuides[1].setGuidelinePercent(progressForTerm(termTicks[2]) / (float) SEEK_MAX);
    }

    private double amountForFraction(double fraction) {
        if (maxAmount <= minAmount || minAmount <= 0) return minAmount;
        return logInterpolate(minAmount, maxAmount, fraction);
    }


    private void applyAmountMaximumFilter() {
        etAmount.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (updatingRanges || restoringState) return null;
                String candidate = dest.subSequence(0, dstart).toString()
                        + source.subSequence(start, end)
                        + dest.subSequence(dend, dest.length());
                candidate = MoneyText.ungroup(candidate.trim()).replace(',', '.');

                if (candidate.isEmpty() || ".".equals(candidate)) return null;
                try {
                    if (Double.parseDouble(candidate) > maxAmount)
                        return dest.subSequence(dstart, dend);
                } catch (NumberFormatException ignored) {
                }
                return null;
            }
        }});
    }

    private void setTicks(TextView[] views, String[] values) {
        for (int i = 0; i < views.length && i < values.length; i++) {
            views[i].setText(values[i]);
        }
    }

    private String formatAmountTick(double amount) {
        if (amount >= 1_000_000_000) {
            double billions = amount / 1_000_000_000.0;
            return String.format(Locale.US, "%.1f млрд ₽", billions).replace(".0", "");
        }
        if (amount >= 1_000_000) {
            double millions = amount / 1_000_000.0;
            return String.format(Locale.US, "%.1f млн ₽", millions).replace(".0", "");
        }
        return String.format(Locale.US, "%,.0f ₽", amount).replace(',', ' ');
    }

    private String formatTermTick(int months) {
        if (months >= 12) {
            int years = months / 12;
            int remainder = months % 12;
            int lastTwo = years % 100;
            int last = years % 10;
            String yearLabel;
            if (lastTwo >= 11 && lastTwo <= 14) yearLabel = years + " лет";
            else if (last == 1) yearLabel = years + " год";
            else if (last >= 2 && last <= 4) yearLabel = years + " года";
            else yearLabel = years + " лет";
            if (remainder == 0) return yearLabel;
            return yearLabel + " " + remainder + " мес";
        }
        return months + " мес";
    }

    private static double logInterpolate(double min, double max, double fraction) {
        if (min <= 0 || max <= min) return min;
        return Math.exp(Math.log(min) + (Math.log(max) - Math.log(min)) * fraction);
    }


    private double amountForProgress(int progress) {
        if (maxAmount <= minAmount || minAmount <= 0) return minAmount;
        int clampedProgress = Math.max(0, Math.min(SEEK_MAX, progress));
        if (clampedProgress == 0) return minAmount;
        if (clampedProgress == SEEK_MAX) return maxAmount;

        if (isVtbCredit()) {
            double middle = getVtbAmountMidpoint();
            if (clampedProgress <= SEEK_MAX / 2) {
                double fraction = clampedProgress / (double) (SEEK_MAX / 2);
                return minAmount + (middle - minAmount) * fraction;
            }
            double fraction = (clampedProgress - SEEK_MAX / 2) / (double) (SEEK_MAX / 2);
            return middle + (maxAmount - middle) * fraction;
        }


        if (isSberDeposit()) {
            if (clampedProgress <= SBER_DEPOSIT_MID_PROGRESS) {
                double fraction = clampedProgress / (double) SBER_DEPOSIT_MID_PROGRESS;
                return logInterpolate(SBER_DEPOSIT_MIN_AMOUNT, SBER_DEPOSIT_MID_AMOUNT, fraction);
            }
            double fraction = (clampedProgress - SBER_DEPOSIT_MID_PROGRESS)
                    / (double) (SEEK_MAX - SBER_DEPOSIT_MID_PROGRESS);
            return logInterpolate(SBER_DEPOSIT_MID_AMOUNT, SBER_DEPOSIT_MAX_AMOUNT, fraction);
        }

        if (isSberCredit()) {
            for (int i = 0; i < SBER_AMOUNT_TICK_PROGRESS.length - 1; i++) {
                int leftProgress = SBER_AMOUNT_TICK_PROGRESS[i];
                int rightProgress = SBER_AMOUNT_TICK_PROGRESS[i + 1];
                if (clampedProgress <= rightProgress) {
                    double fraction = (clampedProgress - leftProgress)
                            / (double) (rightProgress - leftProgress);
                    return logInterpolate(SBER_AMOUNT_TICKS[i], SBER_AMOUNT_TICKS[i + 1], fraction);
                }
            }
            return SBER_AMOUNT_TICKS[SBER_AMOUNT_TICKS.length - 1];
        }

        double fraction = clampedProgress / (double) SEEK_MAX;
        return logInterpolate(minAmount, maxAmount, fraction);
    }

    private int progressForAmount(double amount) {
        if (maxAmount <= minAmount || minAmount <= 0 || amount <= 0) return 0;
        double clamped = Math.max(minAmount, Math.min(maxAmount, amount));

        if (isVtbCredit()) {
            double middle = getVtbAmountMidpoint();
            if (clamped <= middle) {
                return (int) Math.round((clamped - minAmount) / (middle - minAmount)
                        * (SEEK_MAX / 2.0));
            }
            return SEEK_MAX / 2 + (int) Math.round((clamped - middle) / (maxAmount - middle)
                    * (SEEK_MAX / 2.0));
        }


        if (isSberDeposit()) {
            if (clamped <= SBER_DEPOSIT_MID_AMOUNT) {
                double fraction = (Math.log(clamped) - Math.log(SBER_DEPOSIT_MIN_AMOUNT))
                        / (Math.log(SBER_DEPOSIT_MID_AMOUNT) - Math.log(SBER_DEPOSIT_MIN_AMOUNT));
                return (int) Math.round(fraction * SBER_DEPOSIT_MID_PROGRESS);
            }
            double fraction = (Math.log(clamped) - Math.log(SBER_DEPOSIT_MID_AMOUNT))
                    / (Math.log(SBER_DEPOSIT_MAX_AMOUNT) - Math.log(SBER_DEPOSIT_MID_AMOUNT));
            return SBER_DEPOSIT_MID_PROGRESS + (int) Math.round(
                    fraction * (SEEK_MAX - SBER_DEPOSIT_MID_PROGRESS));
        }

        if (isSberCredit()) {
            for (int i = 0; i < SBER_AMOUNT_TICKS.length - 1; i++) {
                double leftAmount = SBER_AMOUNT_TICKS[i];
                double rightAmount = SBER_AMOUNT_TICKS[i + 1];
                if (clamped <= rightAmount) {
                    double fraction = (Math.log(clamped) - Math.log(leftAmount))
                            / (Math.log(rightAmount) - Math.log(leftAmount));
                    double progress = SBER_AMOUNT_TICK_PROGRESS[i] + fraction
                            * (SBER_AMOUNT_TICK_PROGRESS[i + 1] - SBER_AMOUNT_TICK_PROGRESS[i]);
                    return (int) Math.round(progress);
                }
            }
            return SEEK_MAX;
        }

        double fraction = (Math.log(clamped) - Math.log(minAmount))
                / (Math.log(maxAmount) - Math.log(minAmount));
        return (int) Math.round(fraction * SEEK_MAX);
    }

    private int termForProgress(int progress) {
        if (isVtbDeposit() && !isVtbAccountSelected()) {
            int clamped = Math.max(0, Math.min(SEEK_MAX, progress));
            int index = (int) Math.round(clamped * (VTB_DEPOSIT_TERMS.length - 1) / (double) SEEK_MAX);
            return VTB_DEPOSIT_TERMS[Math.max(0, Math.min(VTB_DEPOSIT_TERMS.length - 1, index))];
        }
        if (isVtbCredit()) {
            int clamped = Math.max(0, Math.min(SEEK_MAX, progress));
            return minTerm + (int) Math.round((maxTerm - minTerm) * clamped / (double) SEEK_MAX);
        }
        if (isSberMortgage()) {
            int clamped = Math.max(0, Math.min(SEEK_MAX, progress));
            if (clamped <= SBER_MORTGAGE_TERM_MID_PROGRESS) {
                double fraction = clamped / (double) SBER_MORTGAGE_TERM_MID_PROGRESS;
                return (int) Math.round(logInterpolate(
                        SBER_MORTGAGE_MIN_TERM, SBER_MORTGAGE_MID_TERM, fraction));
            }
            double fraction = (clamped - SBER_MORTGAGE_TERM_MID_PROGRESS)
                    / (double) (SEEK_MAX - SBER_MORTGAGE_TERM_MID_PROGRESS);
            return (int) Math.round(logInterpolate(
                    SBER_MORTGAGE_MID_TERM, SBER_MORTGAGE_MAX_TERM, fraction));
        }
        if (isSberDeposit()) {
            int clamped = Math.max(0, Math.min(SEEK_MAX, progress));
            for (int segment = 0; segment < SBER_DEPOSIT_TERM_ANCHOR_PROGRESS.length - 1; segment++) {
                int leftProgress = SBER_DEPOSIT_TERM_ANCHOR_PROGRESS[segment];
                int rightProgress = SBER_DEPOSIT_TERM_ANCHOR_PROGRESS[segment + 1];
                if (clamped <= rightProgress) {
                    double fraction = (clamped - leftProgress) / (double) (rightProgress - leftProgress);
                    int leftIndex = SBER_DEPOSIT_TERM_ANCHOR_INDEX[segment];
                    int rightIndex = SBER_DEPOSIT_TERM_ANCHOR_INDEX[segment + 1];
                    int index = (int) Math.round(leftIndex + fraction * (rightIndex - leftIndex));
                    return SBER_DEPOSIT_TERMS[index];
                }
            }
            return SBER_DEPOSIT_TERMS[SBER_DEPOSIT_TERMS.length - 1];
        }
        if (maxTerm <= minTerm || minTerm <= 0) return minTerm;
        double fraction = Math.max(0, Math.min(SEEK_MAX, progress)) / (double) SEEK_MAX;
        return (int) Math.round(logInterpolate(minTerm, maxTerm, fraction));
    }

    private int progressForTerm(int months) {
        if (isVtbDeposit() && !isVtbAccountSelected()) {
            int index = 0;
            int distance = Integer.MAX_VALUE;
            for (int i = 0; i < VTB_DEPOSIT_TERMS.length; i++) {
                int candidate = Math.abs(VTB_DEPOSIT_TERMS[i] - months);
                if (candidate < distance) {
                    distance = candidate;
                    index = i;
                }
            }
            return (int) Math.round(index * SEEK_MAX / (double) (VTB_DEPOSIT_TERMS.length - 1));
        }
        if (isVtbCredit()) {
            if (maxTerm <= minTerm) return 0;
            int clamped = Math.max(minTerm, Math.min(maxTerm, months));
            return (int) Math.round((clamped - minTerm) / (double) (maxTerm - minTerm) * SEEK_MAX);
        }
        if (isSberMortgage()) {
            int clamped = Math.max(SBER_MORTGAGE_MIN_TERM,
                    Math.min(SBER_MORTGAGE_MAX_TERM, months));
            if (clamped <= SBER_MORTGAGE_MID_TERM) {
                double fraction = (Math.log(clamped) - Math.log(SBER_MORTGAGE_MIN_TERM))
                        / (Math.log(SBER_MORTGAGE_MID_TERM) - Math.log(SBER_MORTGAGE_MIN_TERM));
                return (int) Math.round(fraction * SBER_MORTGAGE_TERM_MID_PROGRESS);
            }
            double fraction = (Math.log(clamped) - Math.log(SBER_MORTGAGE_MID_TERM))
                    / (Math.log(SBER_MORTGAGE_MAX_TERM) - Math.log(SBER_MORTGAGE_MID_TERM));
            return SBER_MORTGAGE_TERM_MID_PROGRESS + (int) Math.round(
                    fraction * (SEEK_MAX - SBER_MORTGAGE_TERM_MID_PROGRESS));
        }
        if (isSberDeposit()) {
            int index = 0;
            int nearestDistance = Integer.MAX_VALUE;
            for (int i = 0; i < SBER_DEPOSIT_TERMS.length; i++) {
                int distance = Math.abs(SBER_DEPOSIT_TERMS[i] - months);
                if (distance < nearestDistance) {
                    index = i;
                    nearestDistance = distance;
                }
            }
            for (int segment = 0; segment < SBER_DEPOSIT_TERM_ANCHOR_INDEX.length - 1; segment++) {
                int leftIndex = SBER_DEPOSIT_TERM_ANCHOR_INDEX[segment];
                int rightIndex = SBER_DEPOSIT_TERM_ANCHOR_INDEX[segment + 1];
                if (index <= rightIndex) {
                    double fraction = (index - leftIndex) / (double) (rightIndex - leftIndex);
                    int leftProgress = SBER_DEPOSIT_TERM_ANCHOR_PROGRESS[segment];
                    int rightProgress = SBER_DEPOSIT_TERM_ANCHOR_PROGRESS[segment + 1];
                    return (int) Math.round(leftProgress + fraction * (rightProgress - leftProgress));
                }
            }
            return SEEK_MAX;
        }
        if (maxTerm <= minTerm || minTerm <= 0 || months <= 0) return 0;
        int clamped = Math.max(minTerm, Math.min(maxTerm, months));
        double fraction = (Math.log(clamped) - Math.log(minTerm))
                / (Math.log(maxTerm) - Math.log(minTerm));
        return (int) Math.round(fraction * SEEK_MAX);
    }

    private boolean isSupportedVtbDepositTerm(int months) {
        for (int value : VTB_DEPOSIT_TERMS) if (value == months) return true;
        return false;
    }

    private boolean isSupportedSberDepositTerm(int months) {
        for (int value : SBER_DEPOSIT_TERMS) if (value == months) return true;
        return false;
    }

    private void syncAmountSliderFromInput() {
        if (seekAmount == null) return;
        double value = parseDouble(etAmount.getText().toString());
        if (value > 0) seekAmount.setProgress(progressForAmount(value));
    }

    private void syncTermSliderFromInput() {
        if (seekTerm == null) return;
        int value = parseInt(etTerm.getText().toString());
        if (value > 0) seekTerm.setProgress(progressForTerm(value));
    }

    private void onAmountOrTermChanged() {
        if (restoringState || updatingRanges) return;
        applyRateFromDb();
        updateLimitsWarning();
        recalculate();
    }


    private void applyRateFromDb() {
        updateRateSource();
        if (product == null || (isAnyMortgage() && !mortgageAmountAvailable)) {
            etRate.setText("");
            return;
        }
        if (switchManualRate.isChecked()) {
            etRate.setText(etManualRate.getText());
            return;
        }
        double amount = parseDouble(etAmount.getText().toString());
        int term = parseInt(etTerm.getText().toString());

        if (isTbankMortgage()) {
            applyTbankMortgageRate(amount, term);
            return;
        }
        if (isVtbMortgage()) {
            applyVtbMortgageRate(amount, term);
            return;
        }
        if (isSberMortgage()) {
            applySberMortgageRate(amount, term);
            return;
        }

        if (isVtbDeposit()) {
            if (isVtbAccountSelected()) {
                double effectiveRate = calculateVtbAccountRate(amount, term);
                etRate.setText(String.format(Locale.US, "%.2f", effectiveRate));
            } else {
                if (spinnerVtbDepositOperation.getSelectedItemPosition() != 0) {
                    etRate.setText("");
                    return;
                }
                if (!isSupportedVtbDepositTerm(term)) {
                    etRate.setText("");
                    return;
                }
                double baseRate = repo.findVtbDepositBaseRate(product.id, amount, term);
                if (baseRate < 0) {
                    etRate.setText("");
                    return;
                }
                double selectedRate = baseRate + getVtbTermDepositBonus();
                selectedRate = Math.min(vtbSettingMaxVkladRate(), selectedRate);
                etRate.setText(String.format(Locale.US, "%.2f", selectedRate));
            }
            return;
        }

        if (isSberDeposit()) {
            if (!isSupportedSberDepositTerm(term)) {
                etRate.setText("");
                return;
            }
            double baseRate = repo.findSberDepositBaseRate(product.id, amount, term,
                    switchDepositTopUp.isChecked());
            if (baseRate < 0) {
                etRate.setText("");
                return;
            }

            double calculatedRate = baseRate;
            if (!switchDepositSalary.isChecked()) calculatedRate -= 0.5;
            if (switchDepositSpending.isChecked()) calculatedRate += 0.5;
            if (switchDepositBalance.isChecked()) calculatedRate += 0.5;
            int payout = spinnerDepositPayout.getSelectedItemPosition();
            if (payout == 1) calculatedRate -= 0.25;
            else if (payout == 2) calculatedRate -= 0.06;
            calculatedRate = Math.max(0.01, Math.min(13.5, calculatedRate));
            etRate.setText(String.format(Locale.US, "%.2f", calculatedRate));
            return;
        }

        RateRule rule = repo.findMatchingRule(product.id, amount, term, getClientSegment());
        if (rule == null) {
            etRate.setText("");
            return;
        }

        String newRate = String.format(Locale.US, "%.2f", rule.ratePercent);
        if (!newRate.equals(etRate.getText().toString())) {
            etRate.setText(newRate);
        }
    }

    private boolean isSberCredit() {
        return bankId == 1 && "Кредит".equals(type);
    }

    private boolean isSberDeposit() {
        return bankId == 1 && "Вклад".equals(type);
    }

    private boolean isVtbCredit() {
        return bankId == 2 && "Кредит".equals(type);
    }

    private boolean isVtbDeposit() {
        return bankId == 2 && "Вклад".equals(type);
    }

    private boolean isVtbAccountSelected() {
        return isVtbDeposit() && spinnerVtbDepositProduct != null
                && spinnerVtbDepositProduct.getSelectedItemPosition() == 1;
    }

    private double vtbSettingMinOnline() {
        return vtbDepositSettings == null ? 10000d : vtbDepositSettings.minOnlineAmount;
    }

    private double vtbSettingMinOffice() {
        return vtbDepositSettings == null ? 50000d : vtbDepositSettings.minOfficeAmount;
    }

    private double vtbSettingMinInterestBalance() {
        return vtbDepositSettings == null ? 1000d : vtbDepositSettings.minInterestBalance;
    }

    private int vtbSettingMinTerm() {
        return vtbDepositSettings == null ? 3 : vtbDepositSettings.minTermMonths;
    }

    private int vtbSettingMaxTerm() {
        return vtbDepositSettings == null ? 36 : vtbDepositSettings.maxTermMonths;
    }

    private double vtbSettingMaxUiAmount() {
        return vtbDepositSettings == null ? 1_000_000_000d : vtbDepositSettings.maxUiAmount;
    }

    private double vtbSettingMaxVkladRate() {
        return vtbDepositSettings == null ? 13.7d : vtbDepositSettings.maxVkladRate;
    }

    private double vtbSettingMaxAccountRate() {
        return vtbDepositSettings == null ? 14.2d : vtbDepositSettings.maxAccountRate;
    }

    private boolean isSberMortgage() {
        return bankId == 1 && "Ипотека".equals(type);
    }

    private boolean isVtbMortgage() {
        return bankId == 2 && "Ипотека".equals(type);
    }

    private boolean isAnyMortgage() {
        return isSberMortgage() || isVtbMortgage() || isTbankMortgage();
    }

    private double nonNegativeInput(EditText field) {
        double value = parseDouble(field.getText().toString());
        return Double.isFinite(value) && value >= 0 ? value : 0;
    }

    private MortgageRateRule activeMortgageRule() {
        if (isTbankMortgage()) return getTbankMortgageRule();
        if (isVtbMortgage()) return getVtbMortgageActiveRule();
        if (product == null) return null;
        return "FAMILY".equals(getMortgageProgramCode())
                ? repo.getFamilyMortgageRule(product.id, getMortgageRegionGroup(), getMortgageChildrenCount())
                : repo.getMortgageProgramRule(product.id, getMortgageProgramCode(), getMortgageRegionGroup());
    }

    private boolean isTbankMortgage() {
        return bankId == 3 && "Ипотека".equals(type);
    }

    private boolean isTbankCredit() {
        return bankId == 3 && "Кредит".equals(type);
    }

    private MortgageRateRule getTbankMortgageRule() {
        return product == null ? null : repo.getMortgageProgramRule(product.id, getMortgageProgramCode(), "ALL");
    }

    private void applyTbankMortgageRate(double price, int months) {
        MortgageRateRule rule = getTbankMortgageRule();
        double down = parseDouble(etDownPayment.getText().toString());
        double principal = price - down;
        boolean valid = rule != null && Double.isFinite(price) && Double.isFinite(down)
                && price >= rule.minPropertyPrice && down >= 0 && down < price
                && down + 0.000001 >= price * rule.minDownPaymentPercent / 100
                && principal >= 500000 && principal <= rule.maxLoan
                && months >= rule.minTermMonths && months <= rule.maxTermMonths;
        etRate.setText(valid ? String.format(Locale.US, "%.2f", rule.ratePercent) : "");
    }

    private String getMortgageProgramCode() {
        int position = spinnerMortgageProgram.getSelectedItemPosition();
        if (isTbankMortgage()) return position == 1 ? "TB_NEW_BUILD" : "TB_SECONDARY";
        if (isVtbMortgage()) {
            switch (position) {
                case 0:
                    return "VTB_SECONDARY";
                case 1:
                    return "VTB_NEW_BUILD";
                case 2:
                    return "VTB_COMBINED";
                case 3:
                    return "VTB_FAMILY";
                case 4:
                    return "VTB_BUILD_HOME";
                case 5:
                    return "VTB_LAND";
                case 6:
                    return "VTB_REFINANCE_LAND";
                case 7:
                    return "VTB_READY_HOME";
                case 8:
                    return "VTB_IT";
                case 9:
                    return "VTB_REFINANCE";
                case 10:
                    return "VTB_MILITARY";
                case 11:
                    return "VTB_MORTGAGED_PROPERTY";
                case 12:
                    return "VTB_MILITARY_REFINANCE";
                case 13:
                    return "VTB_PARKING";
                case 14:
                    return "VTB_STORAGE";
                case 15:
                    return "VTB_COMMERCIAL";
                default:
                    return "VTB_SECONDARY";
            }
        }
        switch (position) {
            case 0:
                return "READY_HOME";
            case 1:
                return "NEW_BUILD";
            case 2:
                return "BUY_HOME";
            case 3:
                return "BUILD_HOME";
            case 4:
                return "LAND_DACHA";
            case 5:
                return "REFINANCE";
            case 6:
                return "GARAGE";
            case 7:
                return "CASH_SECURED";
            case 8:
                return "COMMERCIAL";
            case 9:
                return "FAMILY";
            case 10:
                return "IT";
            default:
                return "READY_HOME";
        }
    }


    private String getMortgageRegionGroup() {
        return isVtbMortgage() ? "PENZA" : "OTHER";
    }

    private int getMortgageChildrenCount() {
        int position = spinnerMortgageChildren.getSelectedItemPosition();
        return position >= 4 ? 5 : Math.max(1, position + 1);
    }

    private boolean isVtbRefinanceProgram(String program) {
        return "VTB_REFINANCE".equals(program)
                || "VTB_REFINANCE_LAND".equals(program)
                || "VTB_MILITARY_REFINANCE".equals(program)
                || "REFINANCE".equals(program)
                || "CASH_SECURED".equals(program);
    }

    private MortgageRateRule getVtbMortgageActiveRule() {
        if (!isVtbMortgage() || product == null) return null;
        String program = getMortgageProgramCode();
        if ("VTB_FAMILY".equals(program)) {
            return repo.getFamilyMortgageRule(product.id, "PENZA", getMortgageChildrenCount());
        }
        return repo.getMortgageProgramRule(product.id, program, "PENZA");
    }


    private double getVtbMortgageMaximumLoan(MortgageRateRule rule) {
        if (rule == null) return 0.0;
        if ("VTB_COMBINED".equals(getMortgageProgramCode())) {
            if (switchMortgageITEligible != null && switchMortgageITEligible.isChecked()) {
                return 18_000_000d;
            }
            boolean familyEligible = switchMortgageChildUnder7 != null
                    && (switchMortgageChildUnder7.isChecked() || switchMortgageDisabledChild.isChecked());
            if (familyEligible) return 15_000_000d;
        }
        return rule.maxLoan;
    }

    private double getMortgageMinimumDownPaymentPercent() {
        String program = getMortgageProgramCode();
        if (isVtbMortgage()) {
            if (isVtbRefinanceProgram(program)) return 0.0;
            if ("VTB_MILITARY".equals(program)) return 5.0;
            if ("VTB_IT".equals(program)) {
                return switchVtbMortgageProjectFunding != null
                        && switchVtbMortgageProjectFunding.isChecked() ? 20.1 : 40.1;
            }
            if ("VTB_COMBINED".equals(program)) {
                boolean familyEligible = switchMortgageChildUnder7 != null
                        && (switchMortgageChildUnder7.isChecked() || switchMortgageDisabledChild.isChecked());
                boolean itEligible = switchMortgageITEligible != null && switchMortgageITEligible.isChecked();
                if (itEligible && !familyEligible) {
                    return switchVtbMortgageProjectFunding != null
                            && switchVtbMortgageProjectFunding.isChecked() ? 20.1 : 40.1;
                }
                return 20.1;
            }
            MortgageRateRule rule = getVtbMortgageActiveRule();
            return rule == null ? 20.1 : rule.minDownPaymentPercent;
        }
        if ("REFINANCE".equals(program) || "CASH_SECURED".equals(program)) return 0.0;
        return 20.1;
    }

    private void updateMortgageOptionVisibility() {
        if (isTbankMortgage()) {
            mortgageFamilySection.setVisibility(View.GONE);
            mortgageStandardSection.setVisibility(View.GONE);
            mortgageITSection.setVisibility(View.GONE);
            vtbMortgageOptionsSection.setVisibility(View.GONE);
            rgMortgageVariant.setVisibility(View.GONE);
            switchMortgageSalary.setVisibility(View.GONE);
            switchMortgageMaternalCapital.setVisibility(View.GONE);
            mortgageMaternalCapitalSection.setVisibility(View.GONE);
            mortgageExtraCostsSection.setVisibility(View.GONE);
            btnMortgageAddCosts.setVisibility(View.GONE);
            btnMortgageNoDownPayment.setVisibility(View.GONE);
            spinnerMortgageCity.setVisibility(View.GONE);
            findViewById(R.id.tvMortgageCityLabel).setVisibility(View.GONE);
            etDownPayment.setVisibility(View.VISIBLE);
            seekDownPayment.setVisibility(View.VISIBLE);
            tvDownPaymentLabel.setVisibility(View.VISIBLE);
            tvDownPaymentPercent.setVisibility(View.VISIBLE);
            tvAmountLabel.setText("Стоимость недвижимости, ₽");
            tvTermLabel.setText("Срок ипотеки, мес.");
            return;
        }
        if (isVtbMortgage()) {
            String program = getMortgageProgramCode();
            boolean family = "VTB_FAMILY".equals(program);
            boolean it = "VTB_IT".equals(program);
            boolean combined = "VTB_COMBINED".equals(program);
            boolean refinance = isVtbRefinanceProgram(program);
            boolean parking = "VTB_PARKING".equals(program);
            boolean storage = "VTB_STORAGE".equals(program);
            boolean commercial = "VTB_COMMERCIAL".equals(program);
            boolean refinanceOnly = refinance;

            vtbMortgageOptionsSection.setVisibility(View.VISIBLE);
            mortgageFamilySection.setVisibility(family || combined ? View.VISIBLE : View.GONE);
            mortgageITSection.setVisibility(it || combined ? View.VISIBLE : View.GONE);
            mortgageStandardSection.setVisibility(View.GONE);
            rgMortgageVariant.setVisibility(View.GONE);
            switchMortgageSalary.setVisibility(View.GONE);
            switchMortgageApprovedTag.setVisibility(View.GONE);
            switchMortgageGuarantee.setVisibility(View.GONE);
            switchMortgageOnlineDeal.setVisibility(View.GONE);
            spinnerMortgagePersonalDiscount.setVisibility(View.GONE);
            switchMortgageTwoUnder18.setVisibility(View.GONE);

            boolean military = "VTB_MILITARY".equals(program) || "VTB_MILITARY_REFINANCE".equals(program);
            boolean salaryDiscounts = !family && !it && !military && !refinanceOnly;
            boolean onlineDiscount = "VTB_SECONDARY".equals(program)
                    || "VTB_MORTGAGED_PROPERTY".equals(program)
                    || "VTB_NEW_BUILD".equals(program)
                    || "VTB_READY_HOME".equals(program)
                    || "VTB_LAND".equals(program) || combined;
            switchVtbMortgageCurrentSalary.setVisibility(salaryDiscounts || combined ? View.VISIBLE : View.GONE);
            switchVtbMortgageNewSalary.setVisibility(salaryDiscounts || combined ? View.VISIBLE : View.GONE);
            switchVtbMortgageOnline.setVisibility(onlineDiscount ? View.VISIBLE : View.GONE);
            switchVtbMortgageInsurance.setVisibility((salaryDiscounts && !commercial) || family || combined
                    || (refinance && !military) ? View.VISIBLE : View.GONE);
            switchVtbMortgageProjectFunding.setVisibility(it || combined ? View.VISIBLE : View.GONE);
            boolean regionalCurrentSalaryDiscount = "VTB_SECONDARY".equals(program)
                    || "VTB_MORTGAGED_PROPERTY".equals(program) || combined;
            if (parking || storage || commercial || "VTB_NEW_BUILD".equals(program)
                    || "VTB_BUILD_HOME".equals(program) || "VTB_LAND".equals(program)
                    || "VTB_READY_HOME".equals(program)) {
                switchVtbMortgageCurrentSalary.setText("Получаю зарплату на карту ВТБ (−0,4 п.п.)");
                switchVtbMortgageNewSalary.setText("Переведу зарплату в ВТБ (−0,4 п.п.)");
            } else if (regionalCurrentSalaryDiscount) {
                switchVtbMortgageCurrentSalary.setText("Получаю зарплату на карту ВТБ (−0,3 п.п.)");
                switchVtbMortgageNewSalary.setText("Переведу зарплату в ВТБ (−0,4 п.п.)");
            } else {
                switchVtbMortgageCurrentSalary.setText("Получаю зарплату на карту ВТБ (−0,4 п.п.)");
                switchVtbMortgageNewSalary.setText("Переведу зарплату в ВТБ (−0,4 п.п.)");
            }
            switchMortgageITEligible.setVisibility(it || combined ? View.VISIBLE : View.GONE);

            boolean hasDownPayment = !refinance;
            etDownPayment.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
            tvDownPaymentLabel.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
            seekDownPayment.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
            etDownPayment.setEnabled(hasDownPayment);
            seekDownPayment.setEnabled(hasDownPayment);
            tvDownPaymentPercent.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
            btnMortgageNoDownPayment.setVisibility(View.GONE);
            switchMortgageMaternalCapital.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
            mortgageMaternalCapitalSection.setVisibility(hasDownPayment && switchMortgageMaternalCapital.isChecked()
                    ? View.VISIBLE : View.GONE);
            btnMortgageAddCosts.setVisibility(View.VISIBLE);
            mortgageExtraCostsSection.setVisibility(mortgageExtraCostsExpanded ? View.VISIBLE : View.GONE);

            if (refinance) tvAmountLabel.setText("Сумма рефинансирования, ₽");
            else if (parking) tvAmountLabel.setText("Стоимость машино-места, ₽");
            else if (storage) tvAmountLabel.setText("Стоимость кладовой, ₽");
            else if (commercial) tvAmountLabel.setText("Стоимость коммерческой недвижимости, ₽");
            else if ("VTB_LAND".equals(program) || "VTB_REFINANCE_LAND".equals(program))
                tvAmountLabel.setText("Стоимость земельного участка, ₽");
            else if ("VTB_BUILD_HOME".equals(program) || "VTB_READY_HOME".equals(program))
                tvAmountLabel.setText("Стоимость дома, ₽");
            else tvAmountLabel.setText("Стоимость недвижимости, ₽");
            return;
        }

        vtbMortgageOptionsSection.setVisibility(View.GONE);
        if (!isSberMortgage()) return;
        String program = getMortgageProgramCode();
        boolean family = "FAMILY".equals(program);
        boolean it = "IT".equals(program);
        boolean standard = !family && !it;
        mortgageFamilySection.setVisibility(family ? View.VISIBLE : View.GONE);
        mortgageStandardSection.setVisibility(View.GONE);
        mortgageITSection.setVisibility(it ? View.VISIBLE : View.GONE);
        if (rbMortgageBase != null) {
            MortgageRateRule displayRule = repo.getMortgageProgramRule(product.id, program, getMortgageRegionGroup());
            double baseRate = displayRule != null ? displayRule.ratePercent : 15.5;
            rbMortgageBase.setText(String.format(Locale.US, "Базовая\nот %.1f%%", baseRate));
            rbMortgageMilitary.setText("Военная\nот 18,6%");
        }
        boolean secondary = "READY_HOME".equals(program);
        switchMortgageSalary.setVisibility(View.GONE);
        switchMortgageApprovedTag.setVisibility(View.GONE);
        switchMortgageGuarantee.setVisibility(View.GONE);
        switchMortgageOnlineDeal.setVisibility(View.GONE);
        spinnerMortgagePersonalDiscount.setVisibility(View.GONE);
        boolean militaryAvailable = "READY_HOME".equals(program) || "NEW_BUILD".equals(program)
                || "BUY_HOME".equals(program) || "BUILD_HOME".equals(program);
        rgMortgageVariant.setVisibility(View.GONE);
        boolean hasDownPayment = !"REFINANCE".equals(program) && !"CASH_SECURED".equals(program);
        etDownPayment.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
        tvDownPaymentLabel.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
        seekDownPayment.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
        etDownPayment.setEnabled(hasDownPayment);
        seekDownPayment.setEnabled(hasDownPayment);
        tvDownPaymentPercent.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
        btnMortgageNoDownPayment.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
        switchMortgageMaternalCapital.setVisibility(hasDownPayment ? View.VISIBLE : View.GONE);
        mortgageMaternalCapitalSection.setVisibility(hasDownPayment && switchMortgageMaternalCapital.isChecked()
                ? View.VISIBLE : View.GONE);
        btnMortgageAddCosts.setVisibility(standard ? View.VISIBLE : View.GONE);
        mortgageExtraCostsSection.setVisibility(standard && mortgageExtraCostsExpanded ? View.VISIBLE : View.GONE);
        if ("REFINANCE".equals(program)) tvAmountLabel.setText("Сумма рефинансируемого кредита, ₽");
        else if ("CASH_SECURED".equals(program))
            tvAmountLabel.setText("Сумма кредита под залог жилья, ₽");
        else tvAmountLabel.setText("Стоимость недвижимости, ₽");
    }

    private void syncDownPaymentSliderFromInput() {
        if (!isAnyMortgage() || etDownPayment == null) return;
        double price = parseDouble(etAmount.getText().toString());
        double downPayment = parseDouble(etDownPayment.getText().toString());
        double maternalCapital = switchMortgageMaternalCapital != null && switchMortgageMaternalCapital.isChecked()
                ? parseDouble(etMortgageMaternalCapital.getText().toString()) : 0.0;
        if (price <= 0 || !Double.isFinite(price) || !Double.isFinite(downPayment)
                || !Double.isFinite(maternalCapital)) {
            tvDownPaymentPercent.setText("Укажите стоимость и первоначальный взнос");
            return;
        }
        double percent = (downPayment + maternalCapital) * 100.0 / price;
        double minPercent = getMortgageMinimumDownPaymentPercent();
        double maxPercent = 80.0;
        double clampedPercent = Math.max(minPercent, Math.min(maxPercent, percent));
        double range = Math.max(0.1, maxPercent - minPercent);
        int progress = (int) Math.round((clampedPercent - minPercent) / range * SEEK_MAX);
        seekDownPayment.setProgress(Math.max(0, Math.min(SEEK_MAX, progress)));
        tvDownPaymentPercent.setText(String.format(Locale.US, "Первоначальный взнос: %.1f%%", percent));
    }

    private boolean editingMortgageAmounts() {
        return etDownPayment.hasFocus() || etMortgageExtraCosts.hasFocus()
                || etMortgageMaternalCapital.hasFocus();
    }

    private void refreshMortgageCalculation() {
        if (restoringState || updatingRanges) return;
        updateSliderRanges();
        applyRateFromDb();
        updateLimitsWarning();
        recalculate();
    }

    private void applyVtbMortgageRate(double propertyPrice, int term) {
        if (product == null) return;
        String program = getMortgageProgramCode();
        double downPayment = parseDouble(etDownPayment.getText().toString());
        double maternal = switchMortgageMaternalCapital.isChecked()
                ? parseDouble(etMortgageMaternalCapital.getText().toString()) : 0.0;
        double downPercent = propertyPrice > 0 ? (downPayment + maternal) * 100.0 / propertyPrice : 0.0;
        double extraCosts = mortgageExtraCostsSection.getVisibility() == View.VISIBLE
                ? parseDouble(etMortgageExtraCosts.getText().toString()) : 0.0;
        boolean refinance = isVtbRefinanceProgram(program);
        double loanAmount = refinance ? propertyPrice + extraCosts
                : propertyPrice - downPayment - maternal + extraCosts;

        MortgageRateRule rule = getVtbMortgageActiveRule();
        if (rule == null || rule.ratePercent <= 0.0) {
            etRate.setText("");
            return;
        }

        if (("VTB_FAMILY".equals(program) || ("VTB_COMBINED".equals(program)
                && !switchMortgageITEligible.isChecked())) && term > 180) {
            etRate.setText("");
            return;
        }
        double rate = rule.ratePercent;
        double maxLoan = rule.maxLoan;
        double minDown = getMortgageMinimumDownPaymentPercent();
        int minMonths = rule.minTermMonths;
        int maxMonths = rule.maxTermMonths;

        if ("VTB_FAMILY".equals(program)) {
            int children = getMortgageChildrenCount();
            boolean childUnder7 = switchMortgageChildUnder7.isChecked();
            boolean disabledChild = switchMortgageDisabledChild.isChecked();
            if (!(childUnder7 || disabledChild)) {
                etRate.setText("");
                return;
            }
            if ((disabledChild && children <= 3) || downPercent >= 50.1) rate = 6.0;
            maxLoan = rule.maxLoan;
            if (!switchVtbMortgageInsurance.isChecked()) rate += 1.0;
        } else if ("VTB_IT".equals(program)) {
            if (!switchMortgageITEligible.isChecked()) {
                etRate.setText("");
                return;
            }
            rate = 6.0;
            maxLoan = 9_000_000d;
            minDown = switchVtbMortgageProjectFunding.isChecked() ? 20.1 : 40.1;
        } else if ("VTB_COMBINED".equals(program)) {
            boolean familyEligible = switchMortgageChildUnder7.isChecked()
                    || switchMortgageDisabledChild.isChecked();
            boolean itEligible = switchMortgageITEligible.isChecked();
            if (!familyEligible && !itEligible) {
                etRate.setText("");
                return;
            }
            double subsidizedLimit;
            double subsidizedRate;
            if (itEligible) {
                subsidizedLimit = 9_000_000d;
                subsidizedRate = 6.0;
                minDown = switchVtbMortgageProjectFunding.isChecked() ? 20.1 : 40.1;
            } else {
                MortgageRateRule familyRule = repo.getFamilyMortgageRule(
                        product.id, "PENZA", getMortgageChildrenCount());
                if (familyRule == null) {
                    etRate.setText("");
                    return;
                }
                subsidizedLimit = familyRule.maxLoan;
                subsidizedRate = switchMortgageDisabledChild.isChecked()
                        && getMortgageChildrenCount() <= 3 ? 6.0 : familyRule.ratePercent;
                minDown = familyRule.minDownPaymentPercent;
            }
            maxLoan = itEligible ? 18_000_000d : 15_000_000d;
            double marketRate = 18.0;
            if (switchVtbMortgageCurrentSalary.isChecked()) marketRate -= 0.3;
            else if (switchVtbMortgageNewSalary.isChecked()) marketRate -= 0.4;
            if (switchVtbMortgageOnline.isChecked()) marketRate -= 0.3;
            if (loanAmount >= 10_000_000d) marketRate -= 0.2;
            if (familyEligible && downPercent >= 50.1) subsidizedRate = 6.0;
            combinedLimit = subsidizedLimit;
            combinedSubsidizedRate = subsidizedRate + (switchVtbMortgageInsurance.isChecked() ? 0 : 1);
            combinedMarketRate = marketRate + (switchVtbMortgageInsurance.isChecked() ? 0 : 1);
            double subsidizedPart = Math.min(loanAmount, subsidizedLimit);
            double marketPart = Math.max(0.0, loanAmount - subsidizedPart);
            rate = loanAmount > 0
                    ? (subsidizedPart * subsidizedRate + marketPart * marketRate) / loanAmount
                    : subsidizedRate;
            if (!switchVtbMortgageInsurance.isChecked()) rate += 1.0;
        } else if ("VTB_MILITARY".equals(program) || "VTB_MILITARY_REFINANCE".equals(program)) {
            rate = rule.ratePercent;
        } else if (refinance) {
            if (!switchVtbMortgageInsurance.isChecked()) rate += 1.0;
        } else if ("VTB_PARKING".equals(program) || "VTB_STORAGE".equals(program)) {
            if (switchVtbMortgageCurrentSalary.isChecked() || switchVtbMortgageNewSalary.isChecked())
                rate -= 0.4;
            if (downPercent >= 50.1) rate -= 0.5;
            if (!switchVtbMortgageInsurance.isChecked()) rate += 1.0;
            rate = Math.max(rule.minRatePercent, rate);
        } else if ("VTB_COMMERCIAL".equals(program)) {
            if (switchVtbMortgageCurrentSalary.isChecked() || switchVtbMortgageNewSalary.isChecked())
                rate -= 0.4;
            rate = Math.max(rule.minRatePercent, rate);
        } else {
            if (switchVtbMortgageCurrentSalary.isChecked()) {
                rate -= ("VTB_SECONDARY".equals(program) || "VTB_MORTGAGED_PROPERTY".equals(program)) ? 0.3 : 0.4;
            } else if (switchVtbMortgageNewSalary.isChecked()) {
                rate -= 0.4;
            }
            boolean onlineDiscountAvailable = "VTB_SECONDARY".equals(program)
                    || "VTB_MORTGAGED_PROPERTY".equals(program)
                    || "VTB_NEW_BUILD".equals(program) || "VTB_READY_HOME".equals(program)
                    || "VTB_LAND".equals(program);
            if (onlineDiscountAvailable && switchVtbMortgageOnline.isChecked()) rate -= 0.3;
            if (!switchVtbMortgageInsurance.isChecked()) rate += 1.0;
            if (loanAmount >= 10_000_000d) rate -= 0.2;
            if ("VTB_SECONDARY".equals(program) || "VTB_MORTGAGED_PROPERTY".equals(program)
                    || "VTB_NEW_BUILD".equals(program) || "VTB_READY_HOME".equals(program)
                    || "VTB_LAND".equals(program)) {
                if (loanAmount >= 6_000_000d) rate -= 0.5;
                if (downPercent >= 50.1) rate -= 0.5;
            }
            rate = Math.max(rule.minRatePercent, rate);
        }

        boolean valid = propertyPrice >= rule.minPropertyPrice
                && term >= minMonths && term <= maxMonths
                && (refinance || downPercent >= minDown)
                && (refinance || downPayment + maternal < propertyPrice)
                && loanAmount >= 300_000d && loanAmount <= maxLoan
                && extraCosts >= 0 && maternal >= 0;
        if (!valid) {
            etRate.setText("");
            return;
        }
        etRate.setText(String.format(Locale.US, "%.2f", Math.max(0.01, rate)));
    }

    private void applySberMortgageRate(double propertyPrice, int term) {
        if (product == null) return;
        String program = getMortgageProgramCode();
        String regionGroup = getMortgageRegionGroup();
        double cashDownPayment = parseDouble(etDownPayment.getText().toString());
        double maternalCapital = switchMortgageMaternalCapital.isChecked()
                ? parseDouble(etMortgageMaternalCapital.getText().toString()) : 0.0;
        double totalDownPayment = cashDownPayment + maternalCapital;
        double downPercent = propertyPrice > 0 ? totalDownPayment * 100.0 / propertyPrice : 0;
        double extraCosts = mortgageExtraCostsSection.getVisibility() == View.VISIBLE
                ? parseDouble(etMortgageExtraCosts.getText().toString()) : 0.0;
        boolean loanWithoutDownPayment = "REFINANCE".equals(program) || "CASH_SECURED".equals(program);
        double loanAmount = loanWithoutDownPayment ? propertyPrice + extraCosts
                : propertyPrice - totalDownPayment + extraCosts;
        if (switchMortgageOnlineDeal != null) {
            boolean onlineDiscountAvailable = loanAmount <= 6_000_000;
            if (!onlineDiscountAvailable && switchMortgageOnlineDeal.isChecked()) {
                switchMortgageOnlineDeal.setChecked(false);
            }
            switchMortgageOnlineDeal.setEnabled(onlineDiscountAvailable);
        }

        MortgageRateRule rule;
        double calculatedRate;
        double maxLoan;
        double minDownPercent;
        int minMonths;
        int maxMonths;

        if ("FAMILY".equals(program)) {
            int children = getMortgageChildrenCount();
            boolean childUnder7 = switchMortgageChildUnder7.isChecked();
            boolean twoUnder18 = switchMortgageTwoUnder18.isChecked() && children >= 2;
            boolean disabledChild = switchMortgageDisabledChild.isChecked();
            if (!(childUnder7 || twoUnder18 || disabledChild)) {
                etRate.setText("");
                return;
            }
            rule = repo.getFamilyMortgageRule(product.id, regionGroup, children);
            if (rule == null) {
                etRate.setText("");
                return;
            }
            calculatedRate = (disabledChild || downPercent > 50.0 || !childUnder7) ? 6.0 : rule.ratePercent;
            maxLoan = rule.maxLoan;
            minDownPercent = rule.minDownPaymentPercent;
            minMonths = rule.minTermMonths;
            maxMonths = rule.maxTermMonths;
        } else if ("IT".equals(program)) {
            if (!switchMortgageITEligible.isChecked()) {
                etRate.setText("");
                return;
            }
            rule = repo.getMortgageProgramRule(product.id, program, regionGroup);
            if (rule == null) {
                etRate.setText("");
                return;
            }
            calculatedRate = rule.ratePercent;
            maxLoan = rule.maxLoan;
            minDownPercent = rule.minDownPaymentPercent;
            minMonths = rule.minTermMonths;
            maxMonths = rule.maxTermMonths;
        } else {
            rule = repo.getMortgageProgramRule(product.id, program, regionGroup);
            if (rule == null) {
                etRate.setText("");
                return;
            }
            boolean militaryMode = rgMortgageVariant.getVisibility() == View.VISIBLE && rbMortgageMilitary.isChecked();
            calculatedRate = militaryMode ? 18.60 : rule.ratePercent;
            maxLoan = rule.maxLoan;
            minDownPercent = rule.minDownPaymentPercent;
            minMonths = rule.minTermMonths;
            maxMonths = rule.maxTermMonths;

            // Нижний опубликованный порог не уменьшаем непроверенными скидками.
            calculatedRate = rule.ratePercent;

        }

        boolean valid = propertyPrice >= rule.minPropertyPrice
                && term >= minMonths && term <= maxMonths
                && (loanWithoutDownPayment || downPercent >= minDownPercent)
                && (loanWithoutDownPayment || totalDownPayment < propertyPrice)
                && loanAmount >= 300000 && loanAmount <= maxLoan
                && cashDownPayment >= 0 && extraCosts >= 0 && maternalCapital >= 0;
        if (!valid) {
            etRate.setText("");
            return;
        }
        etRate.setText(String.format(Locale.US, "%.2f", Math.max(0.01, calculatedRate)));
    }

    private void refreshDepositCalculation() {
        if (restoringState) return;
        applyRateFromDb();
        updateLimitsWarning();
        recalculate();
    }

    private String getClientSegment() {
        if (isTbankCredit()) {
            return parseInt(etTerm.getText().toString()) > 60
                    || parseDouble(etAmount.getText().toString()) > 5000000 ? "TB_REALTY" : "ALL";
        }
        if (isVtbCredit()) {
            if (switchVtbPensioner != null && switchVtbPensioner.isChecked())
                return "VTB_PENSIONER";
            if (switchVtbSalaryClient != null && switchVtbSalaryClient.isChecked())
                return "VTB_SALARY";
            return "VTB_GENERAL";
        }
        if (!isSberCredit()) return "ALL";
        return switchSalaryClient != null && switchSalaryClient.isChecked()
                ? "SALARY_PENSION" : "GENERAL";
    }

    private void refreshVtbCreditConditions() {
        if (restoringState) return;
        updateSliderRanges();
        applyRateFromDb();
        updateLimitsWarning();
        recalculate();
    }

    /**
     * Всегда показывает только допустимый диапазон суммы и срока.
     */
    private void updateLimitsWarning() {
        if (tvWarning == null) return;
        if (isAnyMortgage()) {
            MortgageRateRule rule = activeMortgageRule();
            double limit = rule == null ? 0 : isVtbMortgage() ? getVtbMortgageMaximumLoan(rule) : rule.maxLoan;
            tvWarning.setText(mortgageAmountAvailable
                    ? "При текущем взносе и расходах: " + fmt(minAmount) + "–" + fmt(maxAmount)
                    + " ₽. Лимит кредита: " + fmt(limit) + " ₽. Чтобы увеличить стоимость объекта, увеличьте взнос."
                    : "Для текущего взноса и расходов нет допустимой суммы. Увеличьте взнос или уменьшите расходы.");
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }
        if (isTbankMortgage()) {
            tvWarning.setText("Первоначальный взнос: от 20,1%. Срок: 1–30 лет. Сумма кредита: 500 000–100 000 000 ₽.");
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }
        if (isTbankCredit()) {
            tvWarning.setText("Срок: 1–15 лет. До 5 лет и 5 млн ₽ — расчёт без залога (от 50 000 ₽). Более 5 лет или 5 млн ₽ — под залог недвижимости: 200 000–30 000 000 ₽.");
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }

        if (isVtbDeposit()) {
            String range = isVtbAccountSelected()
                    ? "Счёт бессрочный; проценты начисляются при остатке от 1 000 ₽. Период прогноза: 1–36 мес."
                    : "Минимум: " + fmt(minAmount) + " ₽; шкала приложения до " + fmt(maxAmount)
                    + " ₽. Доступные сроки: 3, 4, 6, 12, 18, 24 и 36 мес.";
            tvWarning.setText(range);
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }

        if (isVtbCredit()) {
            tvWarning.setText("Диапазон суммы: " + fmt(minAmount) + "–" + fmt(maxAmount)
                    + " ₽. Срок: " + formatTermTick(minTerm) + "–" + formatTermTick(maxTerm) + ".");
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }

        if (isVtbMortgage()) {
            MortgageRateRule activeRule = getVtbMortgageActiveRule();
            double minDp = getMortgageMinimumDownPaymentPercent();
            double maxLoan = activeRule == null ? 0.0 : getVtbMortgageMaximumLoan(activeRule);
            String limit = activeRule == null ? "" : " Лимит кредита: до " + fmt(maxLoan) + " ₽.";
            tvWarning.setText("Пенза. Стоимость объекта: " + fmt(minAmount) + "–" + fmt(maxAmount)
                    + " ₽. Первоначальный взнос: от " + String.format(Locale.US, "%.1f", minDp)
                    + "%. Срок: " + formatTermTick(minTerm) + "–" + formatTermTick(maxTerm) + "." + limit);
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }

        if (isSberMortgage()) {
            String maxTermLabel = "1–30 лет";
            String dpRange = ("REFINANCE".equals(getMortgageProgramCode()) || "CASH_SECURED".equals(getMortgageProgramCode()))
                    ? "Первоначальный взнос не используется в этой модели. "
                    : "Первоначальный взнос: от 20,1% (включая материнский капитал). ";
            String amountLabel = "REFINANCE".equals(getMortgageProgramCode())
                    ? "Сумма рефинансирования" : "CASH_SECURED".equals(getMortgageProgramCode())
                    ? "Сумма кредита" : "Стоимость объекта";
            tvWarning.setText(amountLabel + ": " + fmt(SBER_MORTGAGE_MIN_AMOUNT) + "–" +
                    fmt(SBER_MORTGAGE_MAX_AMOUNT) + " ₽. " + dpRange +
                    "Срок: " + maxTermLabel + "; сумма кредита ограничена выбранной программой.");
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setVisibility(View.VISIBLE);
            return;
        }

        List<RateRule> rules = getActiveRules();
        if (rules.isEmpty()) {
            tvWarning.setVisibility(View.GONE);
            return;
        }

        double allowedMinAmount = Double.MAX_VALUE;
        double allowedMaxAmount = -1;
        int allowedMinTerm = Integer.MAX_VALUE;
        int allowedMaxTerm = -1;
        for (RateRule rule : rules) {
            allowedMinAmount = Math.min(allowedMinAmount, rule.minAmount);
            allowedMaxAmount = Math.max(allowedMaxAmount, rule.maxAmount);
            allowedMinTerm = Math.min(allowedMinTerm, rule.minTermMonths);
            allowedMaxTerm = Math.max(allowedMaxTerm, rule.maxTermMonths);
        }

        allowedMinAmount = minAmount;
        allowedMaxAmount = maxAmount;
        allowedMinTerm = minTerm;
        allowedMaxTerm = maxTerm;
        String rangeText;
        if (isSberDeposit()) {
            rangeText = String.format(Locale.US,
                    "Диапазон суммы: %s–%s ₽. Доступные сроки: 1–12, 18, 24 или 36 мес.",
                    fmt(allowedMinAmount), fmt(allowedMaxAmount));
        } else {
            rangeText = String.format(Locale.US,
                    "Диапазон суммы: %s–%s ₽. Срок: %d–%d мес.",
                    fmt(allowedMinAmount), fmt(allowedMaxAmount), allowedMinTerm, allowedMaxTerm);
        }

        tvWarning.setTextColor(COLOR_HINT);
        tvWarning.setText(rangeText);
        tvWarning.setVisibility(View.VISIBLE);
    }

    private void recalculate() {
        try {
            recalculateChecked();
        } catch (IllegalArgumentException error) {
            tvResult.setText("Проверьте сумму, срок и ставку: нужны конечные неотрицательные числа.");
        }
    }

    private void recalculateChecked() {
        double amount = parseDouble(etAmount.getText().toString());
        int term = parseInt(etTerm.getText().toString());
        double rate = parseDouble(etRate.getText().toString());

        if (amount <= 0 || term <= 0 || rate < 0 || !Double.isFinite(amount)
                || !Double.isFinite(rate) || etRate.getText().toString().trim().isEmpty()) {
            tvResult.setText("Проверьте ввод и условия программы. Если точная ставка неизвестна, включите «Своя ставка» и введите номинальную ставку из предложения банка.");
            return;
        }

        if (amount < minAmount || amount > maxAmount || term < minTerm || term > maxTerm) {
            tvResult.setText("Сумма или срок вне диапазона выбранной модели. См. ограничения выше.");
            return;
        }
        if ("Вклад".equals(type)) {
            if (isVtbDeposit()) {
                calculateVtbDepositResult(amount, rate, term);
                return;
            }
            String payoutMode = bankId == 3 ? "MONTHLY_CAPITALIZATION" : "END";
            if (isSberDeposit()) {
                int selected = spinnerDepositPayout.getSelectedItemPosition();
                payoutMode = selected == 1 ? "MONTHLY_ACCOUNT"
                        : selected == 2 ? "MONTHLY_CAPITALIZATION" : "END";
            }
            DepositResult result = DepositCalculator.calculate(amount, rate, term, payoutMode);
            tvResult.setText(String.format(Locale.US,
                    "Сумма вклада и всех выплат: %s ₽\nПроценты до налога: %s ₽",
                    MoneyText.output(result.finalAmount), MoneyText.output(result.interest)));
        } else if ("Ипотека".equals(type)) {
            CreditResult result;
            double principal = amount;
            if (isAnyMortgage()) {
                String program = getMortgageProgramCode();
                double downPayment = parseDouble(etDownPayment.getText().toString());
                double maternalCapital = switchMortgageMaternalCapital.isChecked()
                        ? parseDouble(etMortgageMaternalCapital.getText().toString()) : 0.0;
                double extraCosts = mortgageExtraCostsSection.getVisibility() == View.VISIBLE
                        ? parseDouble(etMortgageExtraCosts.getText().toString()) : 0.0;
                if (isVtbRefinanceProgram(program)) {
                    downPayment = 0;
                    maternalCapital = 0;
                    principal = amount + extraCosts;
                } else {
                    principal = amount - downPayment - maternalCapital + extraCosts;
                }
                if (!Double.isFinite(downPayment) || !Double.isFinite(maternalCapital)
                        || !Double.isFinite(extraCosts) || downPayment < 0 || maternalCapital < 0
                        || extraCosts < 0 || (!isVtbRefinanceProgram(program)
                        && downPayment + maternalCapital >= amount) || principal <= 0) {
                    tvResult.setText("Проверьте первоначальный взнос, маткапитал и расходы.");
                    return;
                }
                result = isVtbMortgage() && "VTB_COMBINED".equals(program) && !switchManualRate.isChecked()
                        ? MortgageCalculator.calculateCombined(principal, combinedLimit,
                        combinedSubsidizedRate, combinedMarketRate, term)
                        : MortgageCalculator.calculate(principal, rate, term);
            } else {
                result = MortgageCalculator.calculate(amount, rate, term);
            }
            tvResult.setText(String.format(Locale.US,
                    "Сумма кредита: %s ₽\nЕжемесячный платёж: %s ₽\nВсего выплат: %s ₽\nПереплата: %s ₽",
                    MoneyText.output(principal), MoneyText.output(result.monthlyPayment), MoneyText.output(result.totalPayment), MoneyText.output(result.overpayment)));
        } else {
            CreditResult result = CreditCalculator.calculate(amount, rate, term);
            tvResult.setText(String.format(Locale.US,
                    "Ежемесячный платёж: %s ₽\nВсего выплат: %s ₽\nПереплата: %s ₽",
                    MoneyText.output(result.monthlyPayment), MoneyText.output(result.totalPayment), MoneyText.output(result.overpayment)));
        }
    }

    /**
     * Надбавки ВТБ-Вклада: «Расти» суммируется только со статусом зарплатного/пенсионного клиента или ВТБ Плюс.
     */
    private double getVtbTermDepositBonus() {
        double bonus = 0.0;
        boolean salaryOrPension = switchVtbDepositSalary.isChecked() || switchVtbDepositPension.isChecked();
        boolean vtbPlus = switchVtbDepositPlus.isChecked();
        boolean newMoneyOrClient = switchVtbDepositNewMoney.isChecked() || switchVtbDepositNewClient.isChecked();

        if (salaryOrPension) {
            bonus += 0.6;
        } else if (vtbPlus) {
            bonus += 0.3;
        } else if (newMoneyOrClient) {
            // Надбавки «Новые деньги» и «Новый вкладчик» альтернативны остальным надбавкам.
            bonus += 1.6;
            return bonus;
        }

        // По условиям ВТБ надбавка «Расти» суммируется со статусом зарплатного/пенсионного клиента или ВТБ Плюс,
        // но не с предложениями «Новые деньги» и «Новый вкладчик».
        if (salaryOrPension || vtbPlus) {
            int rasti = spinnerVtbDepositRasti.getSelectedItemPosition();
            if (rasti == 1) bonus += 0.25;
            else if (rasti == 2) bonus += 0.5;
        }
        return bonus;
    }

    private void syncVtbRastiAvailability() {
        if (!isVtbDeposit() || spinnerVtbDepositRasti == null) return;
        boolean accountMode = isVtbAccountSelected();
        boolean statusAllowsRasti = switchVtbDepositSalary.isChecked()
                || switchVtbDepositPension.isChecked() || switchVtbDepositPlus.isChecked();
        boolean enabled = accountMode || statusAllowsRasti;
        spinnerVtbDepositRasti.setEnabled(enabled);
        if (!enabled && spinnerVtbDepositRasti.getSelectedItemPosition() != 0) {
            spinnerVtbDepositRasti.setSelection(0);
        }
    }

    private void refreshVtbDepositCalculation() {
        if (!isVtbDeposit()) return;
        updateSliderRanges();
        applyRateFromDb();
        updateLimitsWarning();
        recalculate();
    }

    /**
     * Условный расчёт ставки накопительного счёта по опубликованным условиям ВТБ.
     */
    private double calculateVtbAccountRate(double amount, int months) {
        boolean minimumBalance = spinnerVtbDepositBalance.getSelectedItemPosition() == 0;
        double baseRate = minimumBalance ? 6.0 : 5.0;
        double regularRate = baseRate;
        if (switchVtbDepositSalary.isChecked() || switchVtbDepositPension.isChecked()
                || switchVtbDepositPlus.isChecked()) regularRate += 2.0;
        int spending = spinnerVtbDepositSpending.getSelectedItemPosition();
        if (spending == 1) regularRate += 1.0;
        else if (spending == 2) regularRate += 4.0;
        int rasti = spinnerVtbDepositRasti.getSelectedItemPosition();
        if (rasti == 1) regularRate += 1.0;
        else if (rasti == 2) regularRate += 1.5;
        double packageCap = spinnerVtbDepositPackage.getSelectedItemPosition() == 1
                ? 10_000_000d : spinnerVtbDepositPackage.getSelectedItemPosition() == 2
                ? 30_000_000d : 1_000_000d;
        double enhancedAmount = Math.min(Math.max(0, amount), packageCap);
        double baseAmount = Math.max(0, amount - enhancedAmount);
        double regularEffective = amount <= 0 ? regularRate
                : (enhancedAmount * regularRate + baseAmount * baseRate) / amount;
        if (!switchVtbAccountWelcome.isChecked()) return regularEffective;
        double welcomeRate = minimumBalance ? 14.2 : 13.2;
        int welcomeMonths = Math.min(2, Math.max(0, months));
        if (months <= 0) return welcomeRate;
        double welcomeEffective = amount <= 0 ? welcomeRate
                : (enhancedAmount * welcomeRate + baseAmount * baseRate) / amount;
        double weighted = (welcomeEffective * welcomeMonths
                + regularEffective * Math.max(0, months - welcomeMonths)) / Math.max(1, months);
        return Math.max(0.0, Math.min(vtbSettingMaxAccountRate(), weighted));
    }


    private void calculateVtbDepositResult(double amount, double rate, int months) {
        if (amount < minAmount || months < minTerm || months > maxTerm) {
            tvResult.setText("");
            return;
        }
        if (isVtbAccountSelected()) {
            if (amount < vtbSettingMinInterestBalance()) {
                tvResult.setText("Проценты начисляются при остатке от 1 000 ₽.");
                return;
            }
            boolean minimumBalance = spinnerVtbDepositBalance.getSelectedItemPosition() == 0;
            double baseRate = minimumBalance ? 6.0 : 5.0;
            double regularRate = baseRate;
            if (switchVtbDepositSalary.isChecked() || switchVtbDepositPension.isChecked()
                    || switchVtbDepositPlus.isChecked()) regularRate += 2.0;
            int spending = spinnerVtbDepositSpending.getSelectedItemPosition();
            if (spending == 1) regularRate += 1.0;
            else if (spending == 2) regularRate += 4.0;
            int rasti = spinnerVtbDepositRasti.getSelectedItemPosition();
            if (rasti == 1) regularRate += 1.0;
            else if (rasti == 2) regularRate += 1.5;
            double packageCap = spinnerVtbDepositPackage.getSelectedItemPosition() == 1
                    ? 10_000_000d : spinnerVtbDepositPackage.getSelectedItemPosition() == 2
                    ? 30_000_000d : 1_000_000d;
            // При ручной ставке используем единый сценарий; надбавки банка не прибавляются.
            DepositResult account = switchManualRate.isChecked()
                    ? DepositCalculator.calculate(amount, rate, months, "MONTHLY_CAPITALIZATION")
                    : SavingsCalculator.calculate(amount, months, baseRate, regularRate,
                    minimumBalance ? 14.2 : 13.2, switchVtbAccountWelcome.isChecked() ? 2 : 0, packageCap);
            double annualizedYield = Math.expm1(Math.log(account.finalAmount / amount) * 12.0 / months) * 100;
            tvResult.setText(String.format(Locale.forLanguageTag("ru-RU"),
                    "Проценты до налога: %s ₽\nИтоговая сумма: %s ₽\nДоходность в годовом выражении: %.2f%%",
                    MoneyText.output(account.interest), MoneyText.output(account.finalAmount), annualizedYield));
            return;
        }
        if (!switchManualRate.isChecked() && !isSupportedVtbDepositTerm(months)) {
            tvResult.setText("Для выбранного срока в используемой таблице нет отдельной ставки.");
            return;
        }
        String payoutMode = spinnerVtbDepositPayout.getSelectedItemPosition() == 1
                ? "MONTHLY_ACCOUNT" : spinnerVtbDepositPayout.getSelectedItemPosition() == 2
                ? "MONTHLY_CAPITALIZATION" : "END";
        DepositResult result = DepositCalculator.calculate(amount, rate, months, payoutMode);
        tvResult.setText(String.format(Locale.US,
                "Проценты до налога: %s ₽\nСумма вклада и всех выплат: %s ₽\nСтавка в расчёте: %.2f%%",
                MoneyText.output(result.interest), MoneyText.output(result.finalAmount), rate));
    }

    private void updateRateSource() {
        if (tvRateSource == null) return;
        String url;
        if (bankId == 1) {
            url = "Вклад".equals(type) ? "https://digital.sber.ru/ru/person/contributions/deposits/vklad"
                    : "Ипотека".equals(type) ? "https://domclick.ru/ipoteka" : "https://digital.sber.ru/ru/person/credits/money";
        } else if (bankId == 2) {
            url = "Вклад".equals(type) ? (isVtbAccountSelected()
                    ? "https://www.vtb.ru/personal/vklady-i-scheta/vtb-schet/"
                    : "https://www.vtb.ru/personal/vklady-i-scheta/vtb-vklad-r/")
                    : "Ипотека".equals(type) ? "https://www.vtb.ru/personal/ipoteka/"
                    : "https://www.vtb.ru/personal/kredit/nalichnymi/";
        } else {
            url = "Вклад".equals(type) ? "https://www.tbank.ru/deposits/" : "Ипотека".equals(type)
                    ? "https://www.tbank.ru/mortgage/secondary-property/"
                    : "https://www.tbank.ru/loans/cash-loan/nopledge/tariffs/";
        }
        String note = "Предварительный расчёт. Окончательные условия определяются банком.";
        if (switchManualRate.isChecked())
            note += " Своя ставка применяется на весь срок без дополнительных надбавок.";
        if (isTbankMortgage()) {
            url = "https://www.tbank.ru/mortgage/";
            note += " Сумма кредита равна стоимости недвижимости за вычетом взноса.";
        }
        if (isTbankCredit() && "TB_REALTY".equals(getClientSegment())) {
            url = "https://www.tbank.ru/loans/cash-loan/realty/";
            note += " Выбран расчёт кредита под залог недвижимости.";
        }
        if (bankId == 3 && "Вклад".equals(type))
            note += " Ежемесячная капитализация; без регулярного пополнения. На 4 месяца лимит 3 млн ₽ с процентами, при отсутствии других таких вкладов.";
        if (isVtbMortgage() && ("VTB_FAMILY".equals(getMortgageProgramCode())
                || ("VTB_COMBINED".equals(getMortgageProgramCode()) && !switchMortgageITEligible.isChecked()))) {
            note += " Семейная ставка рассчитывается на срок до 15 лет.";
        }
        tvRateSource.setText(note + "\nУсловия продукта: " + url);
        android.text.util.Linkify.addLinks(tvRateSource, android.text.util.Linkify.WEB_URLS);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        mortgageExtraCostsExpanded = state.getBoolean("extraCostsExpanded", false);
        restoringState = true;
        boolean mortgageReady = mortgageControlsReady, depositReady = depositControlsReady;
        boolean vtbReady = vtbDepositControlsReady;
        mortgageControlsReady = depositControlsReady = vtbDepositControlsReady = false;
        try {
            super.onRestoreInstanceState(state);
        } finally {
            restoringState = false;
            mortgageControlsReady = mortgageReady;
            depositControlsReady = depositReady;
            vtbDepositControlsReady = vtbReady;
        }
        etAmount.post(() -> {
            updateMortgageOptionVisibility();
            updateSliderRanges();
            onAmountOrTermChanged();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("extraCostsExpanded", mortgageExtraCostsExpanded);
        super.onSaveInstanceState(state);
    }

    private static double parseDouble(String value) {
        try {
            String normalized = value.trim().replace(" ", "").replace("\u00a0", "")
                    .replace("\u202f", "").replace(',', '.');
            double parsed = Double.parseDouble(normalized);
            return Double.isFinite(parsed) ? parsed : Double.NaN;
        } catch (NumberFormatException ignored) {
            return Double.NaN;
        }
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static String fmt(double value) {
        return String.format(Locale.US, "%,.0f", value).replace(',', ' ');
    }
}
