import React from 'react';
import TagListContainer from './modules/tag-list/containers/TagListContainer';
import Report from './models/Report';
import logo from './logo.svg';
import './App.css';
import tagListData from './resources/tag-response.json';

const App = () => {
    const dummyReport = new Report(tagListData);
    return (
        <div className="xbdd-app">
            <header className="xbdd-app-header">
                <img src={logo} className="xbdd-app-logo" alt="logo" />
                <h1 className="xbdd-app-title">Welcome to React</h1>
            </header>
            <p className="xbdd-app-intro">
                To get started, edit <code>src/App.js</code> and save to reload.
            </p>
            <TagListContainer report={dummyReport} />
        </div>
    );
};

export default App;