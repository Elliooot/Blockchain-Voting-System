import React, { useState } from 'react'; // 1. 匯入 useState
import './Vote.css';

function Vote() {
  const TaskCard = ({ category = "Title", description = "Description", showImage = true, completed = false }) => {
    const [isExpanded, setIsExpanded] = useState(false);

    const handleToggleExpand = () => {
      setIsExpanded(!isExpanded);
    };

    return (
      <div className="task-card">
        <h2 className="task-category" title={category}>
          {category}
        </h2>
        
        <div 
          className={`task-description ${isExpanded ? 'expanded' : ''}`}
          onClick={handleToggleExpand}
        >
          {description}
        </div>
        
        {showImage && (
          <div className="task-image-placeholder">
            <div className="placeholder-icon">
              ⊞
            </div>
          </div>
        )}
        
        <div className="divider" />
        
        <div className="task-actions">
          <button className="action-button">+</button> {/* Pin function */}
          <button className="action-button">📅</button> {/* Alarm function */}
          <button className="action-button">✏️</button> {/* Note function */}
          <button className="action-button">📋</button> {/* Mayby Flag function */}
        </div>
      </div>
    );
  };
  
  return (
    <div className="vote-container">      
      <div className="columns-container">
        {/* Upcoming Column */}
        <div className="column">
          <div className="column-header">
            <h3 className="column-title">Upcoming</h3>
            <button className="icon-button">
              <span className="more-icon">⋯</span>
            </button>
          </div>
          <TaskCard category='Election 1 Election 2' description="It's Election Day in the bustling constituency of Oakhaven. Voters are heading to the polls to decide their next Member of Parliament. A tight race is anticipated between the incumbent, a long-standing community advocate, and a passionate newcomer promising radical change. Key issues dominating the campaign include local infrastructure, healthcare funding, and environmental policy. Turnout is expected to be high as residents eagerly cast their ballots in this crucial democratic exercise."/>
          <TaskCard />
          <TaskCard />
          {/* <TaskCard showImage={false} /> */}
        </div>

        {/* In Progress Column */}
        <div className="column">
          <div className="column-header">
            <h3 className="column-title">In Progress</h3>
            <button className="icon-button">
              <span className="more-icon">⋯</span>
            </button>
          </div>
          <TaskCard />
          <TaskCard />
        </div>

        {/* Completed Column */}
        <div className="column">
          <div className="column-header">
            <h3 className="column-title">Completed</h3>
            <button className="icon-button">
              <span className="more-icon">⋯</span>
            </button>
          </div>
          <TaskCard />
          <TaskCard />
        </div>
      </div>
    </div>
  );
}

export default Vote;