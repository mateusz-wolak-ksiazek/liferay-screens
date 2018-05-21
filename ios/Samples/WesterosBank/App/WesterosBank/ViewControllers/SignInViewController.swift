/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import UIKit
import LiferayScreens

class SignInViewController: CardViewController,
		LoginScreenletDelegate,
		ForgotPasswordScreenletDelegate,
		KeyboardListener {

	@IBOutlet weak var scroll: UIScrollView!
	@IBOutlet weak var forgotTitle: UIButton!
	@IBOutlet weak var backArrow: UIImageView!

	@IBOutlet weak var signInPage: UIView!
	@IBOutlet weak var forgotPage: UIView!

	@IBOutlet weak var loginScreenlet: LoginScreenlet!
	@IBOutlet weak var forgotPasswordScreenlet: ForgotPasswordScreenlet!

	override init(card: CardView, nibName: String) {
		let save = card.minimizedHeight
		card.minimizedHeight = 0
		super.init(card: card, nibName: nibName)
		card.minimizedHeight = save
	}

	convenience init(card: CardView) {
		self.init(card: card, nibName: "SignInViewController")

		loginScreenlet.viewModel.userName = "test@liferay.com"
		loginScreenlet.viewModel.password = "test"
	}

	required init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)
	}

	override func viewDidLoad() {
		scroll.contentSize = CGSize(width: scroll.frame.size.width * 2, height: scroll.frame.size.height)

		signInPage.frame = scroll.frame
		forgotPage.frame = CGRectMake(scroll.frame.size.width,
			y: scroll.frame.origin.y,
			size: scroll.frame.size)

		self.loginScreenlet.delegate = self
		self.forgotPasswordScreenlet.delegate = self

		self.forgotPasswordScreenlet.anonymousApiUserName =
				LiferayServerContext.propertyForKey("anonymousUsername") as? String
		self.forgotPasswordScreenlet.anonymousApiPassword =
				LiferayServerContext.propertyForKey("anonymousPassword") as? String
	}

	override func viewWillAppear(_ animated: Bool) {
		if cardView!.button!.superview !== scroll {
			cardView!.button!.removeFromSuperview()
			scroll.addSubview(cardView!.button!)
		}
	}

	@IBAction func backAction(_ sender: AnyObject) {
		UIView.animate(withDuration: 0.3,
				animations: {
					self.forgotTitle.alpha = 0.0
					self.backArrow.alpha = 0.0
					self.cardView?.arrow?.alpha = 1.0
				},
				completion: nil)

		let newRect = CGRectMake(0, y: 0, size: scroll.frame.size)
		scroll.scrollRectToVisible(newRect, animated: true)
	}

	@IBAction func forgotPasswordAction(_ sender: AnyObject) {
		self.forgotTitle.alpha = 0.0
		self.backArrow.alpha = 0.0

		UIView.animate(withDuration: 0.5,
				animations: {
					self.forgotTitle.alpha = 1.0
					self.backArrow.alpha = 1.0
					self.cardView?.arrow?.alpha = 0.0
				},
				completion: nil)

		let newRect = CGRectMake(scroll.frame.size.width, y: 0, size: scroll.frame.size)
		scroll.scrollRectToVisible(newRect, animated: true)
	}

	func screenlet(_ screenlet: BaseScreenlet, onLoginResponseUserAttributes attributes: [String: AnyObject]) {
		onDone?()
	}

	func screenlet(_ screenlet: ForgotPasswordScreenlet, onForgotPasswordSent passwordSent: Bool) {
		backAction(self)
	}

	override func cardWillAppear() {
		registerKeyboardListener(self)
	}

	override func cardWillDisappear() {
		unregisterKeyboardListener(self)
	}

	func showKeyboard(_ notif: Notification) {
		if cardView?.currentState == .normal {
			cardView?.nextState = .maximized
			cardView?.changeToNextState()
		}
	}

	func hideKeyboard(_ notif: Notification) {
		if cardView?.currentState == .maximized {
			cardView?.nextState = .normal
			cardView?.changeToNextState()
		}
	}

}
