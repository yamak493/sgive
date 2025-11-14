# sgive

Folia対応の簡易Give補助プラグインです。

このプラグインは、サーバー管理者（OP）向けに、オンライン／オフライン問わずプレイヤーにアイテムを付与するための簡易コマンドを提供します。

## 概要

- コマンド: `/sgive <player> <item_spec> [amount]`
	- `item_spec` は `/give` に渡せる形式の文字列（JSONコンポーネントをサポート）です。
	- 例: `/sgive プレイヤー名 アイテム名 個数`
- オンラインプレイヤー：サーバー側でコンソールから `/give` を実行して付与します（Foliaのグローバルスケジューラーを使用してメインスレッドで実行します）。
- オフラインプレイヤー：`pending_items.yml` に保存し、そのプレイヤーが次回ログインした際に付与します。

## 権限

- `sgive.use` — デフォルトで OP のみ実行可能。

## ファイル

- プラグインデータフォルダに `pending_items.yml` が作成されます。オフラインで保留したアイテム情報を保存します。

## インストール

1. リリースまたはGithub AcionsからJarファイルをダウンロードしてください。
- [最新のリリース](https://github.com/yamak493/sgive/releases/latest)
- [Github Actions](https://github.com/yamak493/sgive/actions)

2. 生成された `sgive-X.X-SNAPSHOT.jar` をサーバーの `plugins` フォルダに配置し、サーバーを再起動またはリロードしてください。