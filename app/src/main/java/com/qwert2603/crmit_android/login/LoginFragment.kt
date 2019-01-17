package com.qwert2603.crmit_android.login

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.text.method.PasswordTransformationMethod
import android.view.*
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.qwert2603.andrlib.base.mvi.BaseFragment
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.CrmitApplication
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.navigation.KeyboardManager
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.UserInputEditText
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.toolbar_default.*

class LoginFragment : BaseFragment<LoginViewState, LoginView, LoginPresenter>(), LoginView {

    override fun createPresenter() = LoginPresenter()

    private lateinit var loginEditText: UserInputEditText
    private lateinit var passwordEditText: UserInputEditText

    private val loggingDialog by lazy {
        AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_logging)
                .setCancelable(false)
                .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_login)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_login)
        loginEditText = UserInputEditText(login_EditText)
        passwordEditText = UserInputEditText(password_EditText)

        password_EditText.typeface = ResourcesCompat.getFont(requireContext(), CrmitApplication.appFontRes)
        password_EditText.transformationMethod = PasswordTransformationMethod()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        loggingDialog.dismiss()
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.login, menu)
        menu.findItem(R.id.about).setOnMenuItemClickListener {
            DiHolder.router.navigateTo(Screen.About)
            true
        }
    }

    override fun loginChanges(): Observable<String> = loginEditText.userInputs()

    override fun passwordChanges(): Observable<String> = passwordEditText.userInputs()

    override fun loginClicks(): Observable<Any> = Observable.merge(
            RxView.clicks(enter_Button),
            RxTextView.editorActionEvents(password_EditText)
                    .filter { currentViewState.isLoginEnabled() }
    )

    override fun render(vs: LoginViewState) {
        super.render(vs)

        loginEditText.setText(vs.login)
        passwordEditText.setText(vs.password)
        enter_Button.isEnabled = vs.isLoginEnabled()

        if (vs.isLogging) {
            (requireActivity() as KeyboardManager).hideKeyboard()
            loggingDialog.show()
        } else {
            loggingDialog.dismiss()
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is LoginViewAction) return
        when (va) {
            is LoginViewAction.ShowLoginError -> Snackbar.make(login_CoordinatorLayout, va.loginErrorReason.descriptionRes, Snackbar.LENGTH_SHORT).show()
            LoginViewAction.MoveToCabinet -> DiHolder.router.newRootScreen(Screen.Cabinet)
        }.also { }
    }
}